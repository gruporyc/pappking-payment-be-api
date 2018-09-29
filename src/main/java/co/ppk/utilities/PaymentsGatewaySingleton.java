package co.ppk.utilities;

import com.payu.sdk.PayU;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static co.ppk.utilities.Constants.*;

@Component
public class PaymentsGatewaySingleton {

    private static PayU instance = null;

    public PaymentsGatewaySingleton() {}

    public static synchronized PayU getInstance() {
        if (instance == null) {
            PayU.paymentsUrl = PAYU_PAIMENTS_URL;
            PayU.reportsUrl = PAYU_REPORTS_URL;
            PayU.apiKey = PAYU_API_KEY;
            PayU.apiLogin = PAYU_API_LOGIN;
            PayU.merchantId = PAYU_MERCHANT_ID;
            PayU.isTest = PAYU_IS_TEST;
        }
        return instance;
    }
}
