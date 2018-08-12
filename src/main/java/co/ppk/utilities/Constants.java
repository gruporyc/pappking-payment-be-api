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
            Integer.valueOf(System.getenv("PAYMENTS_MAX_PENDING_HOURS")) : null)
            .orElse(10);

	public static final int MAX_PENDING_TIME = Optional.ofNullable(
			!Objects.isNull(System.getenv("PAYMENTS_MAX_PENDING_HOURS")) ?
					Integer.valueOf(System.getenv("PAYMENTS_MAX_PENDING_HOURS")) : null)
			.orElse(48);

	public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

	public static final String DATABASE_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public static final String LOAD_TRANSACTION_PREFIX = "LOAD_TRANSACTION_";

    public static final String TRANSACTION_DESCRIPTION = "Recarga de saldo pappking";

	private Constants() {
	}

	/* validations */
	
}
