package co.ppk.service;

import co.ppk.domain.Balance;
import co.ppk.domain.Load;
import co.ppk.domain.Service;
import co.ppk.dto.LoadRequestDto;
import co.ppk.dto.PaymentDto;
import co.ppk.enums.Country;
import co.ppk.enums.CreditCardType;

import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface BusinessManager {

    Load loadPayment(LoadRequestDto load, MeatadataBO metadata) throws NoSuchAlgorithmException;

    List<com.payu.sdk.model.Bank> getBanks(Country country);

    void checkPendingPayments();

    boolean ping();

    boolean payService(PaymentDto payment);

    Balance getBalance(String customerId);

    Service getService(String serviceId);

    List<CreditCardType> getCreditCardTypes();
}
