package co.ppk.validators;

import co.ppk.domain.Client;
import co.ppk.dto.ClientDto;
import co.ppk.dto.PaymentDto;
import co.ppk.enums.Codes;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

@Component
public class ClientValidator extends BaseValidator implements Validator {

    /*
     * This method validates the login data
     *
     * @return void no value is returning.
     */
    @Override
    public void validate(Object target, Errors errors) {

        ClientDto client = (ClientDto) target;
        //TODO: Implement client object validations
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return ClientDto.class.equals(clazz);
    }
}