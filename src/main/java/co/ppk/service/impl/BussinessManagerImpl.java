package co.ppk.service.impl;

import co.ppk.data.ApiKeysRepository;
import co.ppk.data.ClientsRepository;
import co.ppk.data.DataSourceSingleton;
import co.ppk.domain.*;
import co.ppk.dto.ApiKeyDto;
import co.ppk.dto.ClientDto;
import co.ppk.dto.LoadRequestDto;
import co.ppk.dto.PaymentDto;
import co.ppk.enums.*;
import co.ppk.service.BusinessManager;
import co.ppk.data.PaymentsRepository;
import co.ppk.service.MeatadataBO;
import com.payu.sdk.PayU;
import com.payu.sdk.PayUPayments;
import com.payu.sdk.PayUReports;
import com.payu.sdk.exceptions.ConnectionException;
import com.payu.sdk.exceptions.InvalidParametersException;
import com.payu.sdk.exceptions.PayUException;
import com.payu.sdk.model.TransactionResponse;
import com.payu.sdk.model.TransactionState;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static co.ppk.utilities.Constants.*;
import static co.ppk.utilities.KeyHelper.loadGatewayKeys;
import static java.lang.Integer.valueOf;

@Component
public class BussinessManagerImpl implements BusinessManager{

    private static final Logger LOGGER = LogManager.getLogger(BussinessManagerImpl.class);

    private static PaymentsRepository paymentsRepository;
    private static ApiKeysRepository apiKeysRepository;
    private static ClientsRepository clientsRepository;

    public BussinessManagerImpl() {
        paymentsRepository = new PaymentsRepository(DataSourceSingleton.getInstance());
        apiKeysRepository = new ApiKeysRepository(DataSourceSingleton.getInstance());
        clientsRepository = new ClientsRepository(DataSourceSingleton.getInstance());
    }

    @Override
    public Load loadPayment(LoadRequestDto load, MeatadataBO metadata, String key) throws NoSuchAlgorithmException {
        String clientId = loadGatewayKeys(key);
        Map<String, String> parameters = new HashMap<>();
        String load_id = UUID.randomUUID().toString();
        double amount = round(load.getAmount(), 2);

/* Create database temporal registry for a new load transaction*/
        String loadId = paymentsRepository.createLoad(new Load.Builder()
                .setAmount(amount)
                .setCurrency(load.getCurrency().name())
                .setCustomerId(load.getBuyer().getId())
                .setMethod(load.getMethod().name())
                .setStatus(Status.INCOMPLETE.name())
                .setCountry(load.getBuyer().getCountry().name())
                .setClientId(clientId)
                .build());

/* Create new load builder to update load with payment transaction result*/
        Load.Builder builder = new Load.Builder()
                .setAmount(amount)
                .setCurrency(load.getCurrency().name())
                .setCustomerId(load.getBuyer().getId())
                .setMethod(load.getMethod().name())
                .setStatus(Status.INCOMPLETE.name())
                .setCountry(load.getBuyer().getCountry().name())
                .setId(loadId)
                .setClientId(clientId);

/* Transaction data. */
        parameters.put(PayU.PARAMETERS.ACCOUNT_ID, PAYMENT_ACCOUNT_ID);
        parameters.put(PayU.PARAMETERS.REFERENCE_CODE, load_id);
        parameters.put(PayU.PARAMETERS.DESCRIPTION, load.getDescription());
        parameters.put(PayU.PARAMETERS.LANGUAGE, "Language.es");
        parameters.put(PayU.PARAMETERS.VALUE, String.valueOf(round(load.getAmount(), 2)));
        parameters.put(PayU.PARAMETERS.CURRENCY, load.getCurrency().name());
        parameters.put(PayU.PARAMETERS.COUNTRY, load.getBuyer().getCountry().name());


/* CASE: cash payment */
        if(load.getMethod().name().contains("CASH")) {
            DateFormat dateFormat = new SimpleDateFormat(DATETIME_FORMAT);
            Date currentDate = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(currentDate);
            c.add(Calendar.HOUR, MAX_PENDING_TIME);
            Date currentDatePlusOne = c.getTime();

            parameters.put(PayU.PARAMETERS.TAX_VALUE, ((valueOf(TAX_VALUE) / 100) > 0) ?
                            String.valueOf((valueOf(TAX_VALUE) / 100) * load.getAmount()) : "0");
            parameters.put(PayU.PARAMETERS.TAX_RETURN_BASE, ((valueOf(TAX_VALUE) / 100) > 0) ?
                    String.valueOf(load.getAmount()/((valueOf(TAX_VALUE) / 100) + 1)) : "0");
            parameters.put(PayU.PARAMETERS.BUYER_EMAIL, load.getBuyer().getEmail());
            parameters.put(PayU.PARAMETERS.PAYER_NAME, load.getBuyer().getName());
            parameters.put(PayU.PARAMETERS.PAYMENT_METHOD, load.getMethod().name().replace("CASH_", ""));
            parameters.put(PayU.PARAMETERS.COUNTRY, load.getBuyer().getCountry().name());
            parameters.put(PayU.PARAMETERS.EXPIRATION_DATE,dateFormat.format(currentDatePlusOne));
        }

/* CASE: Transfer payment */
        if(load.getMethod().name().equals("PSE")) {

/* Payer data */
            parameters.put(PayU.PARAMETERS.PAYER_NAME, load.getBuyer().getName());
            parameters.put(PayU.PARAMETERS.PAYER_EMAIL, load.getBuyer().getEmail());
            parameters.put(PayU.PARAMETERS.PAYER_CONTACT_PHONE, load.getBuyer().getPhone());

/* PSE data */
            parameters.put(PayU.PARAMETERS.PSE_FINANCIAL_INSTITUTION_CODE, load.getFinancialInstituteCode());
            parameters.put(PayU.PARAMETERS.PAYER_PERSON_TYPE, load.getPersonType().name());
            parameters.put(PayU.PARAMETERS.PAYER_DNI, load.getDni());
            parameters.put(PayU.PARAMETERS.PAYER_DOCUMENT_TYPE, load.getDocumentType().name());
            parameters.put(PayU.PARAMETERS.RESPONSE_URL, RESPONSE_URL);

            parameters.put(PayU.PARAMETERS.PAYMENT_METHOD, load.getMethod().name());
        }

/* CASE: Credit card payment */
        if(load.getMethod().name().equals("CREDIT")) {
/* Buyer data */
            parameters.put(PayU.PARAMETERS.BUYER_ID, load.getBuyer().getId());
            parameters.put(PayU.PARAMETERS.BUYER_NAME, load.getBuyer().getName());
            parameters.put(PayU.PARAMETERS.BUYER_EMAIL, load.getBuyer().getEmail());
            parameters.put(PayU.PARAMETERS.BUYER_CONTACT_PHONE, load.getBuyer().getPhone());
            parameters.put(PayU.PARAMETERS.BUYER_DNI, load.getBuyer().getIdentification());
            parameters.put(PayU.PARAMETERS.BUYER_STREET, load.getBuyer().getAddress1());
            parameters.put(PayU.PARAMETERS.BUYER_STREET_2, load.getBuyer().getAddress2());
            parameters.put(PayU.PARAMETERS.BUYER_CITY, load.getBuyer().getCity());
            parameters.put(PayU.PARAMETERS.BUYER_STATE, load.getBuyer().getState());
            parameters.put(PayU.PARAMETERS.BUYER_COUNTRY, load.getBuyer().getCountry().name());
            parameters.put(PayU.PARAMETERS.BUYER_POSTAL_CODE, load.getBuyer().getPostalCode());
            parameters.put(PayU.PARAMETERS.BUYER_PHONE, load.getBuyer().getPhone());

// Payer data
            parameters.put(PayU.PARAMETERS.PAYER_NAME, load.getBuyer().getName());
            parameters.put(PayU.PARAMETERS.PAYER_EMAIL, load.getBuyer().getEmail());
            parameters.put(PayU.PARAMETERS.PAYER_CONTACT_PHONE, load.getBuyer().getPhone());
            parameters.put(PayU.PARAMETERS.PAYER_DNI, load.getBuyer().getIdentification());
            parameters.put(PayU.PARAMETERS.PAYER_STREET, load.getBuyer().getAddress1());
            parameters.put(PayU.PARAMETERS.PAYER_STREET_2, load.getBuyer().getAddress2());
            parameters.put(PayU.PARAMETERS.PAYER_CITY, load.getBuyer().getCity());
            parameters.put(PayU.PARAMETERS.PAYER_STATE, load.getBuyer().getState());
            parameters.put(PayU.PARAMETERS.PAYER_COUNTRY, load.getBuyer().getCountry().name());
            parameters.put(PayU.PARAMETERS.PAYER_POSTAL_CODE, load.getBuyer().getPostalCode());
            parameters.put(PayU.PARAMETERS.PAYER_PHONE, load.getBuyer().getPhone());

// Credit card data
            parameters.put(PayU.PARAMETERS.CREDIT_CARD_NUMBER, load.getCreditCard().getNumber());
            parameters.put(PayU.PARAMETERS.CREDIT_CARD_EXPIRATION_DATE, load.getCreditCard().getExpiration());
            parameters.put(PayU.PARAMETERS.CREDIT_CARD_SECURITY_CODE, String.valueOf(load.getCreditCard().getSecurityCode()));
            parameters.put(PayU.PARAMETERS.PAYMENT_METHOD, load.getCreditCard().getType().name());
            parameters.put(PayU.PARAMETERS.INSTALLMENTS_NUMBER, String.valueOf(load.getCreditCard().getInstallments()));
            parameters.put(PayU.PARAMETERS.COUNTRY, load.getCreditCard().getCountry().name());

            String creditDigits = load.getMethod().name().equals("CREDIT") ?
                    load.getCreditCard().getNumber().substring(load.getCreditCard().getNumber().length() - 3) : "";
            builder.setPayerName(!Objects.isNull(load.getBuyer().getName()) ? load.getBuyer().getName() : "")
                    .setPayerCardLastDigits(!Objects.isNull(load.getCreditCard().getNumber()) ? creditDigits : "");
        }

/* Transaction metadata. */
        MessageDigest mdEnc = MessageDigest.getInstance("MD5");
        String textToDigest = PAYU_API_KEY + "~" +
                PAYU_MERCHANT_ID + "~" +
                load_id + "~" +
                String.valueOf(amount) + "~" +
                load.getCurrency().name();
        mdEnc.update(textToDigest.getBytes());

        byte[] digest = mdEnc.digest();

        StringBuffer sb = new StringBuffer();
        for (byte aDigest : digest) sb.append(Integer.toString((aDigest & 0xff) + 0x100, 16).substring(1));

        parameters.put(PayU.PARAMETERS.SIGNATURE, sb.toString());
        parameters.put(PayU.PARAMETERS.DEVICE_SESSION_ID, metadata.getSessionId());
        parameters.put(PayU.PARAMETERS.IP_ADDRESS, metadata.getRemoteIp());
        parameters.put(PayU.PARAMETERS.COOKIE, metadata.getCookie());
        parameters.put(PayU.PARAMETERS.USER_AGENT, metadata.getUserAgent());

/* Starting to get transaction response from payment gateway */
        TransactionResponse transactionResponse;
        try {
            transactionResponse = PayUPayments.doAuthorizationAndCapture(parameters);
            if (transactionResponse.getState().equals(TransactionState.PENDING)){
                builder.setBankUrl(String.valueOf(transactionResponse.getExtraParameters().get("BANK_URL")));
                builder.setReceiptUrl(String.valueOf(transactionResponse.getExtraParameters().get("URL_PAYMENT_RECEIPT_HTML")));
            }

            builder.setNetworkCode(transactionResponse.getPaymentNetworkResponseCode())
                    .setNetworkMessage(transactionResponse.getPaymentNetworkResponseErrorMessage())
                    .setOrderId(String.valueOf(transactionResponse.getOrderId()))
                    .setResponseCode(transactionResponse.getResponseCode().name())
                    .setTransactionId(transactionResponse.getTransactionId())
                    .setStatus(transactionResponse.getState().name())
                    .setTrazabilityCode(transactionResponse.getTrazabilityCode());
        } catch (PayUException e) {
            e.printStackTrace();
            throw new HttpClientErrorException(org.springframework.http.HttpStatus.METHOD_FAILURE); //420
        } catch (InvalidParametersException e) {
            e.printStackTrace();
            throw new HttpClientErrorException(HttpStatus.NOT_ACCEPTABLE, e.getMessage()); //443
        } catch (ConnectionException e) {
            e.printStackTrace();
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR); //500
        }

        Load loadUpdated = builder.build();
        paymentsRepository.updateLoad(loadUpdated);
        if(loadUpdated.getMethod().equals(PaymentMethod.PSE.name()) ||
                loadUpdated.getMethod().equals(PaymentMethod.CASH_BALOTO.name()) ||
                loadUpdated.getMethod().equals(PaymentMethod.CASH_EFECTY.name())) {
            Optional<ApiKey> apiKey = apiKeysRepository.getApiKeyById(key);
            clientsRepository.updateClientStatus(Status.PENDING.name(), apiKey.get().getClientId());
        }
        return loadUpdated;
    }

    @Override
    public List<com.payu.sdk.model.Bank> getBanks(Country country, String key) {
        loadGatewayKeys(key);
        Map<String, String> parameters = new HashMap<>();

        parameters.put(PayU.PARAMETERS.PAYMENT_METHOD, PaymentMethod.PSE.name());
        parameters.put(PayU.PARAMETERS.COUNTRY, Country.CO.name());

        List<com.payu.sdk.model.Bank> banks;
        try {
            banks = PayUPayments.getPSEBanks(parameters);
        } catch (PayUException e) {
            e.printStackTrace();
            throw new HttpClientErrorException(org.springframework.http.HttpStatus.METHOD_FAILURE); //420
        } catch (InvalidParametersException e) {
            e.printStackTrace();
            throw new HttpClientErrorException(HttpStatus.NOT_ACCEPTABLE, e.getMessage()); //443
        } catch (ConnectionException e) {
            e.printStackTrace();
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR); //500
        }
        return banks;
    }

    @Override
    public void checkPendingPayments() {
        List<Client> clients = clientsRepository.getClientsByStatus(Status.PENDING.name());
        if(clients.isEmpty()) {
            System.out.println("No loads pending for conciliate.");
        }
        if(clients.isEmpty()) { return; }

        for (Client client : clients) {
            List<Load> pendingLoads = paymentsRepository.getLoadsByStatus(Status.PENDING.name(), client.getId());
            System.out.println("Loads pending to conciliate for client " + client.getId() + ": " + pendingLoads.size());
            for (Load load: pendingLoads) {
                try {
                    DateFormat dateFormat = new SimpleDateFormat(DATABASE_DATETIME_FORMAT);
                    Calendar loadDate = Calendar.getInstance();
                    loadDate.setTime(dateFormat.parse(load.getCreatedAt()));
                    loadDate.add(Calendar.HOUR, MAX_PENDING_TIME);
                    if(loadDate.getTime().before(new Date())) {
                        paymentsRepository.updateLoadStatus(load.getId(), load.getClientId(), Status.DISMISSED.name());
                        continue;
                    }

// If status is APPROVED then update balance with new amount
                    Status status = checkOrder(load.getTransactionId(), client);
                    if (Objects.isNull(status)) {
                        continue;
                    }
                    if(status.name().equals(Status.APPROVED.name())) {
                        paymentsRepository.updateBalance(load.getCustomerId(), load.getClientId(), round(load.getAmount(), 2));
                        paymentsRepository.updateLoadStatus(load.getId(), load.getClientId(), Status.APPROVED.name());
                    } else if (!status.equals(Status.PENDING)){
                        paymentsRepository.updateLoadStatus(load.getId(), load.getClientId(), status.name());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public String createClient(ClientDto client) {
        return clientsRepository.createClient(new Client.Builder()
                .setName(client.getName())
                .setGatewayApiKey(client.getGatewayApiKey())
                .setGatewayApiLogin(client.getGatewayApiLogin())
                .setGatewayMerchantId(client.getGatewayMerchantId())
                .setGatewayAccountId(client.getGatewayAccoutId())
                .build()
        );
    }

    @Override
    public String createApiKey(ApiKeyDto apiKey) {
        //TODO: generate token for API key
        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        keyGen.init(128);
        SecretKey secretKey = keyGen.generateKey();

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date expDate = DateUtils.addDays(now, apiKey.getValidity());
        JwtBuilder builder = Jwts.builder()
                .setId(apiKey.getClientId())
                .setIssuedAt(now)
                .setSubject("")
                .signWith(signatureAlgorithm, secretKey)
                .setExpiration(expDate);

        //TODO: generate expiration date based on validity
        //TODO: apply client status validation to throw exception if client have invalid status


        String token = builder.compact();
        apiKeysRepository.createApiKey(token, apiKey.getClientId(), new Timestamp(expDate.getTime()));
        return token;
    }

    public boolean ping(String key) {
        try {
            loadGatewayKeys(key);
            return PayUPayments.doPing();
        } catch (PayUException e) {
            e.printStackTrace();
            throw new HttpClientErrorException(org.springframework.http.HttpStatus.METHOD_FAILURE); //420
        } catch (ConnectionException e) {
            e.printStackTrace();
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR); //500
        }
    }

    public boolean payService(PaymentDto payment, String key) {
        String clientId = loadGatewayKeys(key);

        if (isPayed(payment.getServiceId(), clientId)) {
            return false;
        }

        Optional<Balance> balance = paymentsRepository.getBalance(payment.getCustomerId(), clientId);
        if (!balance.isPresent()) {
            throw new HttpClientErrorException(HttpStatus.PRECONDITION_FAILED, "no balance present");
        }
        if(balance.get().getBalance() < payment.getAmount()) {
            throw new HttpClientErrorException(HttpStatus.PRECONDITION_FAILED, "balance insufficient");
        }
        if(!balance.get().getStatus().equals(Status.ACTIVE.name())) {
            throw new HttpClientErrorException(HttpStatus.PRECONDITION_FAILED, "balance not active");
        }

        Service service = new Service.Builder()
                .setAmount(payment.getAmount())
                .setClientId(clientId)
                .setServiceId(payment.getServiceId())
                .setCustomerId(payment.getCustomerId())
                .build();
        paymentsRepository.createServicePayment(service);
        return true;
    }

    public Balance getBalance(String customerId, String key) {
        String clientId = loadGatewayKeys(key);
        Optional<Balance> balance = paymentsRepository.getBalance(customerId, clientId);
        if(!balance.isPresent()) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
        return balance.get();
    }

    public Service getService(String serviceId, String key) {
        String clientId = loadGatewayKeys(key);
        Optional<Service> service = paymentsRepository.getServiceById(serviceId, clientId);
        if(!service.isPresent()) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
        return service.get();
    }

    public List<CreditCardType> getCreditCardTypes() {
        return Stream.of(CreditCardType.values())
                .filter(ct -> ct != CreditCardType.UNRECOGNIZED).collect(Collectors.toList());
    }

    private boolean isPayed(String serviceId, String clientId) {
        Optional<Service> service = paymentsRepository.getServiceById(serviceId, clientId);
        if (service.isPresent() && service.get().getStatus().equals(Status.APPROVED.name())) {
            return true;
        }
        return false;
    }

    private Status checkOrder(String transactionId, Client client) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put(PayU.PARAMETERS.TRANSACTION_ID, transactionId);
        Status statusResponse = null;

        LOGGER.info("Starting to check status for transaction: " + transactionId);

        try {
            PayU.paymentsUrl = PAYU_PAIMENTS_URL;
            PayU.reportsUrl = PAYU_REPORTS_URL;
            PayU.apiKey = client.getGatewayApiKey();
            PayU.apiLogin = client.getGatewayApiLogin();
            PayU.merchantId = client.getGatewayMerchantId();
            PayU.isTest = PAYU_IS_TEST;
            TransactionResponse response = PayUReports.getTransactionResponse(parameters);
            statusResponse = Status.valueOf(response.getState().name());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusResponse;
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
