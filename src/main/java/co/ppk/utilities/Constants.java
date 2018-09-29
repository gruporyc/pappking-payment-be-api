package co.ppk.utilities;

import com.payu.sdk.PayU;

import java.util.Objects;
import java.util.Optional;

public class Constants {

	
	/* Custom Headers */

	public static final String LANGUAGE_HEADER = "language";
	public static final String CLIENT_ID_HEADER = "x-client-id";

	public static final String EMAIL_REQUEST_PARAM = "email";
	public static final String PSSWRD_REQUEST_PARAM = "password";
	public static final String CUSTOMER_REQUEST_PARAM = "customer_id";



	public static final String PAYMENT_ACCOUNT_ID = Optional.ofNullable(System.getenv("PAYMENTS_ACCOUNT_ID"))
			.orElse("512321");

	public static final String TAX_VALUE = Optional.ofNullable(System.getenv("PAYMENTS_TAX_VALUE"))
			.orElse("19");

	public static final String RESPONSE_URL = Optional.ofNullable(System.getenv("PAYMENTS_RESPONSE_URL"))
			.orElse("http://www.pappking.com/payment-response/");

    public static final int CHECK_PAYMENTS_STATUS_INTERVAL = Optional.ofNullable(!Objects.isNull(System.getenv("PAYMENTS_CHECK_INTERVAL_MINUTES")) ?
            Integer.valueOf(System.getenv("PAYMENTS_CHECK_INTERVAL_MINUTES")) : null)
            .orElse(10);

	public static final int MAX_PENDING_TIME = Optional.ofNullable(
			!Objects.isNull(System.getenv("PAYMENTS_MAX_PENDING_HOURS")) ?
					Integer.valueOf(System.getenv("PAYMENTS_MAX_PENDING_HOURS")) : null)
			.orElse(48);

	public static final String PAYU_PAIMENTS_URL = Optional.ofNullable(System.getenv("PAYMENTS_RESPONSE_URL"))
			.orElse("https://sandbox.api.payulatam.com/payments-api/");

	public static final String PAYU_REPORTS_URL = Optional.ofNullable(System.getenv("PAYMENTS_REPORTS_RESPONSE_URL"))
			.orElse("https://sandbox.api.payulatam.com/reports-api/");

	public static final String PAYU_API_KEY = Optional.ofNullable(System.getenv("PAYMENTS_API_KEY"))
			.orElse("4Vj8eK4rloUd272L48hsrarnUA");

	public static final String PAYU_API_LOGIN = Optional.ofNullable(System.getenv("PAYMENTS_API_LOGIN"))
			.orElse("pRRXKOl8ikMmt9u");

	public static final String PAYU_MERCHANT_ID = Optional.ofNullable(System.getenv("PAYMENTS_MERCHAN_ID"))
			.orElse("508029");

	public static final boolean PAYU_IS_TEST = Optional.ofNullable(Boolean.valueOf(System.getenv("PAYMENTS_TEST_PAYMENT")))
			.orElse(false);

	public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

	public static final String DATABASE_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public static final String LOAD_TRANSACTION_PREFIX = "LOAD_TRANSACTION_";

    public static final String TRANSACTION_DESCRIPTION = "Recarga de saldo pappking";

	private Constants() {
	}

	/* validations */
	
}
