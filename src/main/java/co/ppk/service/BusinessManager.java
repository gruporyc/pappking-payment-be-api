package co.ppk.service;

import co.ppk.domain.Balance;
import co.ppk.domain.Load;
import co.ppk.domain.Service;
import co.ppk.dto.ApiKeyDto;
import co.ppk.dto.ClientDto;
import co.ppk.dto.LoadRequestDto;
import co.ppk.dto.PaymentDto;
import co.ppk.enums.Country;
import co.ppk.enums.CreditCardType;

import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface BusinessManager {

    Load loadPayment(LoadRequestDto load, MeatadataBO metadata, String key) throws NoSuchAlgorithmException;

    List<com.payu.sdk.model.Bank> getBanks(Country country, String key);

    void checkPendingPayments();

    boolean ping(String key);

    boolean payService(PaymentDto payment);

    Balance getBalance(String customerId);

    Service getService(String serviceId);

    List<CreditCardType> getCreditCardTypes();

    String createClient(ClientDto client);

    String createApiKey(ApiKeyDto apiKey);
}
