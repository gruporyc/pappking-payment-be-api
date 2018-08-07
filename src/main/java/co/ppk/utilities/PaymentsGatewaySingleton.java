package co.ppk.utilities;

import com.payu.sdk.PayU;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PaymentsGatewaySingleton {

    private static PayU instance = null;

    public PaymentsGatewaySingleton() {}

    public static synchronized PayU getInstance() {
        if (instance == null) {
            PayU.paymentsUrl = Optional.ofNullable(System.getenv("PAYMENTS_RESPONSE_URL"))
                    .orElse("https://sandbox.api.payulatam.com/payments-api/");
            PayU.reportsUrl = Optional.ofNullable(System.getenv("PAYMENTS_REPORTS_RESPONSE_URL"))
                    .orElse("https://sandbox.api.payulatam.com/reports-api/");
            PayU.apiKey = Optional.ofNullable(System.getenv("PAYMENTS_API_KEY"))
                    .orElse("4Vj8eK4rloUd272L48hsrarnUA");
            PayU.apiLogin = Optional.ofNullable(System.getenv("PAYMENTS_API_LOGIN"))
                    .orElse("pRRXKOl8ikMmt9u");
            PayU.merchantId = Optional.ofNullable(System.getenv("PAYMENTS_MERCHAN_ID"))
                    .orElse("508029");
            PayU.isTest = Optional.ofNullable(Boolean.getBoolean(System.getenv("PAYMENTS_TEST_PAYMENT")))
                    .orElse(false);
        }
        return instance;
    }
}
