package co.ppk.utilities;

import com.payu.sdk.PayU;

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

	private Constants() {
	}

	/* validations */
	
}
