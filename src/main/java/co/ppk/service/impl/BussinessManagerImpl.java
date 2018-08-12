package co.ppk.service.impl;

import co.ppk.domain.Balance;
import co.ppk.domain.Load;
import co.ppk.domain.Service;
import co.ppk.dto.LoadRequestDto;
import co.ppk.dto.PaymentDto;
import co.ppk.enums.Country;
import co.ppk.enums.Currency;
import co.ppk.enums.PaymentMethod;
import co.ppk.enums.Status;
import co.ppk.service.BusinessManager;
import co.ppk.data.PaymentsRepository;
import co.ppk.utilities.PaymentsGatewaySingleton;
import com.payu.sdk.PayU;
import com.payu.sdk.PayUPayments;
import com.payu.sdk.PayUReports;
import com.payu.sdk.exceptions.ConnectionException;
import com.payu.sdk.exceptions.InvalidParametersException;
import com.payu.sdk.exceptions.PayUException;
import com.payu.sdk.model.TransactionResponse;
import com.payu.sdk.model.TransactionState;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static co.ppk.utilities.Constants.*;

@Component
public class BussinessManagerImpl implements BusinessManager{

    private static PaymentsRepository paymentsRepository;

    public BussinessManagerImpl() {
        paymentsRepository = new PaymentsRepository();
    }

    @Override
    public Load loadPayment(LoadRequestDto load) {
        PaymentsGatewaySingleton.getInstance();
        Map<String, String> parameters = new HashMap<>();
        String load_id = UUID.randomUUID().toString();

        String loadId = paymentsRepository.createLoad(new Load.Builder()
                .setAmount(load.getAmount())
                .setCurrency(load.getCurrency().name())
                .setCustomerId(load.getBuyer().getId())
                .setMethod(load.getMethod().name())
                .setStatus(Status.INCOMPLETE.name())
                .setCountry(load.getBuyer().getCountry().name())
                .build());

        Load.Builder builder = new Load.Builder()
                .setAmount(load.getAmount())
                .setCurrency(load.getCurrency().name())
                .setCustomerId(load.getBuyer().getId())
                .setMethod(load.getMethod().name())
                .setStatus(Status.INCOMPLETE.name())
                .setCountry(load.getBuyer().getCountry().name())
                .setId(loadId);

//Transaction data.
        parameters.put(PayU.PARAMETERS.ACCOUNT_ID, PAYMENT_ACCOUNT_ID);
        parameters.put(PayU.PARAMETERS.REFERENCE_CODE, LOAD_TRANSACTION_PREFIX + load_id);
        parameters.put(PayU.PARAMETERS.DESCRIPTION, TRANSACTION_DESCRIPTION);
        parameters.put(PayU.PARAMETERS.LANGUAGE, "Language.es");
        parameters.put(PayU.PARAMETERS.VALUE, String.valueOf(load.getAmount()));
        parameters.put(PayU.PARAMETERS.CURRENCY, Currency.COP.name());
        parameters.put(PayU.PARAMETERS.COUNTRY, load.getBuyer().getCountry().name());


// CASE: cash payment
        if(load.getMethod().name().contains("CASH")) {
            DateFormat dateFormat = new SimpleDateFormat(DATETIME_FORMAT);
            Date currentDate = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(currentDate);
            c.add(Calendar.HOUR, MAX_PENDING_TIME);
            Date currentDatePlusOne = c.getTime();

            parameters.put(PayU.PARAMETERS.TAX_VALUE, ((Integer.valueOf(TAX_VALUE) / 100) > 0) ?
                            String.valueOf((Integer.valueOf(TAX_VALUE) / 100) * load.getAmount()) : "0");
            parameters.put(PayU.PARAMETERS.TAX_RETURN_BASE, ((Integer.valueOf(TAX_VALUE) / 100) > 0) ?
                    String.valueOf(load.getAmount()/((Integer.valueOf(TAX_VALUE) / 100) + 1)) : "0");
            parameters.put(PayU.PARAMETERS.BUYER_EMAIL, load.getBuyer().getEmail());
            parameters.put(PayU.PARAMETERS.PAYER_NAME, load.getPayer().getName());
            parameters.put(PayU.PARAMETERS.PAYMENT_METHOD, load.getMethod().name().replace("CASH_", ""));
            parameters.put(PayU.PARAMETERS.COUNTRY, load.getCreditCard().getCountry().name());
            parameters.put(PayU.PARAMETERS.EXPIRATION_DATE,dateFormat.format(currentDatePlusOne));
        }

// CASE: Transfer payment
        if(load.getMethod().name().equals("PSE")) {

// Payer data
            parameters.put(PayU.PARAMETERS.PAYER_NAME, load.getPayer().getName());
            parameters.put(PayU.PARAMETERS.PAYER_EMAIL, load.getPayer().getEmail());
            parameters.put(PayU.PARAMETERS.PAYER_CONTACT_PHONE, load.getPayer().getPhone());

//PSE data
            parameters.put(PayU.PARAMETERS.PSE_FINANCIAL_INSTITUTION_CODE, load.getFinancialInstituteCode());
            parameters.put(PayU.PARAMETERS.PAYER_PERSON_TYPE, load.getPersonType().name());
            parameters.put(PayU.PARAMETERS.PAYER_DNI, load.getDni());
            parameters.put(PayU.PARAMETERS.PAYER_DOCUMENT_TYPE, load.getDocumentType().name());
            parameters.put(PayU.PARAMETERS.RESPONSE_URL, RESPONSE_URL);

            parameters.put(PayU.PARAMETERS.PAYMENT_METHOD, load.getMethod().name());
        }

        if(load.getMethod().name().equals("CREDIT")) {
// Buyer data
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
            parameters.put(PayU.PARAMETERS.PAYER_NAME, load.getPayer().getName());
            parameters.put(PayU.PARAMETERS.PAYER_EMAIL, load.getPayer().getEmail());
            parameters.put(PayU.PARAMETERS.PAYER_CONTACT_PHONE, load.getPayer().getPhone());
            parameters.put(PayU.PARAMETERS.PAYER_DNI, load.getPayer().getIdentification());
            parameters.put(PayU.PARAMETERS.PAYER_STREET, load.getPayer().getAddress1());
            parameters.put(PayU.PARAMETERS.PAYER_STREET_2, load.getPayer().getAddress2());
            parameters.put(PayU.PARAMETERS.PAYER_CITY, load.getPayer().getCity());
            parameters.put(PayU.PARAMETERS.PAYER_STATE, load.getPayer().getState());
            parameters.put(PayU.PARAMETERS.PAYER_COUNTRY, load.getPayer().getCountry().name());
            parameters.put(PayU.PARAMETERS.PAYER_POSTAL_CODE, load.getPayer().getPostalCode());
            parameters.put(PayU.PARAMETERS.PAYER_PHONE, load.getPayer().getPhone());

// Credit card data
            parameters.put(PayU.PARAMETERS.CREDIT_CARD_NUMBER, load.getCreditCard().getNumber());
            parameters.put(PayU.PARAMETERS.CREDIT_CARD_EXPIRATION_DATE, load.getCreditCard().getExpiration());
            parameters.put(PayU.PARAMETERS.CREDIT_CARD_SECURITY_CODE, String.valueOf(load.getCreditCard().getSecurityCode()));
            parameters.put(PayU.PARAMETERS.PAYMENT_METHOD, load.getCreditCard().getType().name());
            parameters.put(PayU.PARAMETERS.INSTALLMENTS_NUMBER, String.valueOf(load.getCreditCard().getInstallments()));
            parameters.put(PayU.PARAMETERS.COUNTRY, load.getCreditCard().getCountry().name());

            String creditDigits = load.getMethod().name().equals("CREDIT") ?
                    load.getCreditCard().getNumber().substring(load.getCreditCard().getNumber().length() - 3) : "";
            builder.setPayerName(!Objects.isNull(load.getPayer().getName()) ? load.getPayer().getName() : "")
                    .setPayerCardLastDigits(!Objects.isNull(load.getCreditCard().getNumber()) ? creditDigits : "");
        }

// Transaction metadata.
        parameters.put(PayU.PARAMETERS.DEVICE_SESSION_ID, "vghs6tvkcle931686k1900o6e1");
        parameters.put(PayU.PARAMETERS.IP_ADDRESS, "127.0.0.1");
        parameters.put(PayU.PARAMETERS.COOKIE, "pt1t38347bs6jc9ruv2ecpv7o2");
        parameters.put(PayU.PARAMETERS.USER_AGENT, "Mozilla/5.0 (Windows NT 5.1; rv:18.0) Gecko/20100101 Firefox/18.0");

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
        paymentsRepository.uppdateLoad(loadUpdated);
        return loadUpdated;
    }


    @Override
    public List<com.payu.sdk.model.Bank> getBanks(Country country) {
        PaymentsGatewaySingleton.getInstance();
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
            String message = e.getMessage();
            throw new HttpClientErrorException(HttpStatus.NOT_ACCEPTABLE, e.getMessage()); //443
        } catch (ConnectionException e) {
            e.printStackTrace();
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR); //500
        }
        return banks;
    }

    @Override
    public void checkPendingPayments() {
        System.out.println("Checking pending payments !!");
        List<Load> pendingLoads = paymentsRepository.getLoadsByStatus(Status.PENDING);
        for (Load load: pendingLoads) {
            try {
                DateFormat dateFormat = new SimpleDateFormat(DATABASE_DATETIME_FORMAT);
                Calendar loadDate = Calendar.getInstance();
                loadDate.setTime(dateFormat.parse(load.getCreatedAt()));
                loadDate.add(Calendar.HOUR, MAX_PENDING_TIME);
                if(loadDate.getTime().before(new Date())) {
                    paymentsRepository.updateLoadStatus(load.getId(), Status.DISMISSED);
                    return;
                }

// If status is APPROVED then update balance with new amount
                Status status = checkOrder(load.getTransactionId());
                if (Objects.isNull(status)) {
                    return;
                }
                if(status.name().equals(Status.APPROVED.name())) {
                    paymentsRepository.updateBalance(load.getCustomerId(), load.getAmount());
                    paymentsRepository.updateLoadStatus(load.getId(), Status.APPROVED);
                } else if (!status.equals(Status.PENDING)){
                    paymentsRepository.updateLoadStatus(load.getId(), status);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean ping() {
        try {
            PaymentsGatewaySingleton.getInstance();
            return PayUPayments.doPing();
        } catch (PayUException e) {
            e.printStackTrace();
            throw new HttpClientErrorException(org.springframework.http.HttpStatus.METHOD_FAILURE); //420
        } catch (ConnectionException e) {
            e.printStackTrace();
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR); //500
        }
    }

    public boolean payService(PaymentDto payment) {
        if (isPayed(payment.getServiceId())) {
            return false;
        }
        Optional<Balance> balance = paymentsRepository.getBalance(payment.getCustomerId());
        if (!balance.isPresent()) {
            throw new HttpClientErrorException(HttpStatus.PRECONDITION_FAILED, "no balance present");
        }
        if(balance.get().getBalance() < payment.getAmount()) {
            throw new HttpClientErrorException(HttpStatus.PRECONDITION_FAILED, "balance insufficient");
        }
        if(!balance.get().getStatus().equals(Status.ACTIVE.name())) {
            throw new HttpClientErrorException(HttpStatus.PRECONDITION_FAILED, "balance not active");
        }

        paymentsRepository.createServicePayment(payment);
        return true;
    }

    public Balance getBalance(String customerId) {
        Optional<Balance> balance = paymentsRepository.getBalance(customerId);
        if(!balance.isPresent()) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
        return balance.get();
    }

    public Service getService(String serviceId) {
        Optional<Service> service = paymentsRepository.getService(serviceId);
        if(!service.isPresent()) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
        return service.get();
    }

    private boolean isPayed(String serviceId) {
        Optional<Service> service = paymentsRepository.getService(serviceId);
        if (service.isPresent()) {
            return true;
        }
        return false;
    }

    private Status checkOrder(String transactionId) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put(PayU.PARAMETERS.TRANSACTION_ID, transactionId);
        Status statusResponse = null;

        try {
            PaymentsGatewaySingleton.getInstance();
            TransactionResponse response = PayUReports.getTransactionResponse(parameters);
            statusResponse = Status.valueOf(response.getState().name());
        } catch (PayUException e) {
            e.printStackTrace();
        } catch (ConnectionException e) {
            e.printStackTrace();
        } catch (InvalidParametersException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return statusResponse;
    }
}
