package co.ppk.exception;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;

/**
 * 
 * 
 * @author jmunoz
 * @version 1.0
 */
public class PpkException extends RootException implements Serializable {

	private int code = HttpStatus.INTERNAL_SERVER_ERROR.value();

	private static final Logger LOGGER = LogManager.getLogger(PpkException.class);
	
	
	/***
	 * custom constructor
	 * 
	 * @param code
	 * @param message
	 */
	public PpkException(int code, String message) {
		super(message);
		
		this.code = code;
		
		LOGGER.info("inside generic exception, code value=" + this.code);
		
	}

	/**
	 * 
	 * @GenericException.java
	 */
	public PpkException() {
		super();
	}

	public PpkException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public PpkException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public PpkException(String arg0) {
		super(arg0);
	}

	public PpkException(Throwable arg0) {
		super(arg0);
	}

	/* (non-Javadoc)
	 * @see ca.eqb.exceptions.RootException#getCode()
	 */
	@Override
	public int getCode() {
		return this.code;
	}

	

}
