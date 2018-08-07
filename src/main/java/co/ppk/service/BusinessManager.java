package co.ppk.service;

import co.ppk.domain.Load;
import co.ppk.dto.LoadRequestDto;
import co.ppk.enums.Country;

import java.util.List;

public interface BusinessManager {

    Load loadPayment(LoadRequestDto load);

    List<com.payu.sdk.model.Bank> getBanks(Country country);
}
