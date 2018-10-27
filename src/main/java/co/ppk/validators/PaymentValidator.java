package co.ppk.validators;

import co.ppk.dto.PaymentDto;
import co.ppk.enums.Codes;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

@Component
public class PaymentValidator extends BaseValidator implements Validator {

    /*
     * This method validates the login data
     *
     * @return void no value is returning.
     */
    @Override
    public void validate(Object target, Errors errors) {

        PaymentDto payment = (PaymentDto) target;
        if (Objects.isNull(payment.getServiceId()) || StringUtils.isEmpty(payment.getServiceId())) {
            errors.rejectValue("service_id", Codes.SERVICE_ID_CANNOT_BE_NULL.getErrorCode());
        }

        if (Objects.isNull(payment.getCustomerId()) || StringUtils.isEmpty(payment.getCustomerId())) {
            errors.rejectValue("service_id", Codes.CUSTOMER_ID_CANNOT_BE_NULL.getErrorCode());
        }

        if (Objects.isNull(payment.getAmount()) || payment.getAmount() <= 0) {
            errors.rejectValue("service_id", Codes.AMOUNT_CANNOT_BE_NULL.getErrorCode());
        }
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return PaymentDto.class.equals(clazz);
    }
}
