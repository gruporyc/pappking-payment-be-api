/******************************************************************
 *  
 * This code is for the Pappking service project.
 *
 * 
 * © 2018, Pappking Management All rights reserved.
 * 
 * 
 ******************************************************************/

package co.ppk.web.controller;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import co.ppk.domain.Load;
import co.ppk.dto.LoadRequestDto;
import co.ppk.enums.Country;
import co.ppk.service.BusinessManager;
import co.ppk.validators.LoadRequestValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import co.ppk.enums.ResponseKeyName;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Only service exposition point of services to FE layer
 * 
 * @author jmunoz
 *
 */

@RestController
@RequestMapping("/v1")
public class ProxyEndpointController extends BaseRestController {

	private static final Logger LOGGER = LogManager.getLogger(ProxyEndpointController.class);

	private static final String CURRENT_USER_LOCALE = "language";

	/** The error properties. */
	@Autowired
	@Qualifier("errorProperties")
	private Properties errorProperties;

	@Autowired
	BusinessManager businessManager;

	@Autowired
	LoadRequestValidator loadRequestValidator;

	/**
	 * entry endpoint receiving the message from messaging API to perform proper action
	 *
	 * @since 30/06/2018
	 *
	 * @author jmunoz
	 * @version 1.0.0
	 */


    @RequestMapping(value = "/payment/load", method = RequestMethod.POST)
    public ResponseEntity<Object> loadPayment(@Validated @RequestBody LoadRequestDto load,
                                                 BindingResult result, HttpServletRequest request) {
		loadRequestValidator.validate(load, result);
		ResponseEntity<Object> responseEntity = apiValidator(result);
		if (responseEntity != null) {
			return responseEntity;
		}

		try {
			Load registry = businessManager.loadPayment(load);
			responseEntity =  ResponseEntity.ok(createSuccessResponse(ResponseKeyName.PAYMENT_RESPONSE, registry));
		} catch (HttpClientErrorException ex) {
			responseEntity = setErrorResponse(ex, request);
		}

		return responseEntity;
    }

	@RequestMapping(value = "/payment/cash/banks/{country}", method = RequestMethod.GET)
	public ResponseEntity<Object> getBanks(@PathVariable("country") String country, HttpServletRequest request, HttpServletResponse response) {

		ResponseEntity responseEntity;
    	try {
			List<com.payu.sdk.model.Bank> banks = businessManager.getBanks(Country.valueOf(country));
			responseEntity =  ResponseEntity.ok(createSuccessResponse(ResponseKeyName.PAYMENT_RESPONSE, banks));
		} catch (HttpClientErrorException ex) {
			responseEntity = setErrorResponse(ex, request);
		}

		return responseEntity;
	}

	private ResponseEntity<Object> setErrorResponse(HttpClientErrorException ex, HttpServletRequest request) {
		HashMap<String, Object> map = new HashMap<>();
		HttpStatus status;
		switch (ex.getStatusCode().value()) {
			case 404:
				map.put("message", "Not Found");
				status = HttpStatus.NOT_FOUND;
				break;
			case 401:
				map.put("message", "Access denied");
				status = HttpStatus.UNAUTHORIZED;
				break;
			case 400:
				map.put("message", "bad request");
				status = HttpStatus.BAD_REQUEST;
				break;
			case 406:
				map.put("message", "invalid parameter");
				map.put("detail", ex.getMessage());
				status = HttpStatus.NOT_ACCEPTABLE;
				break;
			case 500:
				status = HttpStatus.INTERNAL_SERVER_ERROR;
				break;
			case 503:
				status = HttpStatus.SERVICE_UNAVAILABLE;
				break;
			default:
				status = HttpStatus.INTERNAL_SERVER_ERROR;
				map.put("message", "There was a problem trying to resolve the request");
		}
		return  ResponseEntity.status(status)
				.body(createLoginFailResponse(ResponseKeyName.PAYMENT_RESPONSE, map, ex));

	}
}
