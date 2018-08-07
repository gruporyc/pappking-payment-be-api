package co.ppk.service.impl;

import co.ppk.domain.Load;
import co.ppk.dto.LoadRequestDto;
import co.ppk.enums.Country;
import co.ppk.enums.Currency;
import co.ppk.enums.PaymentMethod;
import co.ppk.service.BusinessManager;
import co.ppk.data.PaymentsRepository;
import co.ppk.utilities.PaymentsGatewaySingleton;
import com.payu.sdk.PayU;
import com.payu.sdk.PayUPayments;
import com.payu.sdk.exceptions.ConnectionException;
import com.payu.sdk.exceptions.InvalidParametersException;
import com.payu.sdk.exceptions.PayUException;
import com.payu.sdk.model.TransactionResponse;
import com.payu.sdk.model.TransactionState;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static co.ppk.utilities.Constants.PAYMENT_ACCOUNT_ID;
import static co.ppk.utilities.Constants.RESPONSE_URL;
import static co.ppk.utilities.Constants.TAX_VALUE;

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

//Transaction data.
        parameters.put(PayU.PARAMETERS.ACCOUNT_ID, PAYMENT_ACCOUNT_ID);
        parameters.put(PayU.PARAMETERS.REFERENCE_CODE, load_id);
        parameters.put(PayU.PARAMETERS.DESCRIPTION, "Load payment " + load_id);
        parameters.put(PayU.PARAMETERS.LANGUAGE, "Language.es");
        parameters.put(PayU.PARAMETERS.VALUE, String.valueOf(load.getAmount()));
        parameters.put(PayU.PARAMETERS.CURRENCY, Currency.COP.name());
        parameters.put(PayU.PARAMETERS.COUNTRY, load.getBuyer().getCountry().name());


// CASE: cash payment
        if(load.getMethod().name().contains("CASH")) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date currentDate = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(currentDate);
            c.add(Calendar.HOUR, 48);
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
        }

// Transaction metadata.
        parameters.put(PayU.PARAMETERS.DEVICE_SESSION_ID, "vghs6tvkcle931686k1900o6e1");
        parameters.put(PayU.PARAMETERS.IP_ADDRESS, "127.0.0.1");
        parameters.put(PayU.PARAMETERS.COOKIE, "pt1t38347bs6jc9ruv2ecpv7o2");
        parameters.put(PayU.PARAMETERS.USER_AGENT, "Mozilla/5.0 (Windows NT 5.1; rv:18.0) Gecko/20100101 Firefox/18.0");

        Load.Builder builder = new Load.Builder();

        try {
            TransactionResponse transactionResponse = PayUPayments.doAuthorizationAndCapture(parameters);
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

        String creditDigits = load.getMethod().name().equals("CREDIT") ? load.getCreditCard().getNumber().substring(load.getCreditCard().getNumber().length() - 3) : "";
        return builder
                .setId(load_id)
                .setAmount(load.getAmount())
                .setCurrency(load.getCurrency())
                .setCustomerId(load.getBuyer().getId())
                .setPayerName(load.getPayer().getName())
                .setPayerCardLastDigits(creditDigits)
                .setMethod(load.getMethod().name())
                .build();

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
}
