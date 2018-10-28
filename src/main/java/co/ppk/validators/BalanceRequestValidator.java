package co.ppk.validators;

import co.ppk.dto.CreateBalanceRequestDto;
import co.ppk.enums.Codes;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

@Component
public class BalanceRequestValidator extends BaseValidator implements Validator {
    @Override
    public void validate(Object target, Errors errors) {

        CreateBalanceRequestDto request = (CreateBalanceRequestDto) target;
        if (Objects.isNull(request.getCustomerId()) || StringUtils.isEmpty(request.getCustomerId())) {
            errors.rejectValue("customer_id", Codes.CUSTOMER_ID_CANNOT_BE_NULL.getErrorCode());
        }
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return CreateBalanceRequestDto.class.equals(clazz);
    }
}
