package co.ppk.service;

import co.ppk.domain.Balance;
import co.ppk.domain.Load;
import co.ppk.domain.Service;
import co.ppk.dto.*;
import co.ppk.enums.*;

import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface BusinessManager {

    Load loadPayment(LoadRequestDto load, MeatadataBO metadata, String key) throws NoSuchAlgorithmException;

    List<com.payu.sdk.model.Bank> getBanks(Country country, String key);

    void checkPendingPayments();

    boolean createCustomerBalanceDto(CreateBalanceRequestDto balanceRequest);

    boolean ping(String key);

    boolean payService(PaymentDto payment);

    Balance getBalance(String customerId);

    PaymentServiceDto getService(String serviceId);

    List<CreditCardType> getCreditCardTypes();

    String createClient(ClientDto client);

    String createApiKey(ApiKeyDto apiKey);

    List<Country> getCountries();

    List<PersonType> getPersonTypes();

    List<PaymentMethod> getPaymentMethods();

    List<DocumentType> getDocumentTypes();

    List<Currency> getCurrencies();
}
