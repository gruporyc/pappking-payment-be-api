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
import java.util.Map;
import java.util.Properties;

import co.ppk.domain.Balance;
import co.ppk.domain.Load;
import co.ppk.domain.Service;
import co.ppk.dto.LoadRequestDto;
import co.ppk.dto.PaymentDto;
import co.ppk.enums.Country;
import co.ppk.service.BusinessManager;
import co.ppk.validators.LoadRequestValidator;
import co.ppk.validators.PaymentValidator;
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

    @Autowired
    PaymentValidator paymentValidator;

	/**
	 * loadPayment method: perform a new money load for an specific customer
	 *
	 * @param load the whole information necessary to perform money load
	 * @author jmunoz
	 * @since 12/08/2018
	 * @version 1.0.0
	 * @return
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

	/**
	 * getBanks method: Retrieve the list of banks supported by application to make wire transfers
	 *
	 * @param country the country where are based banks list
	 * @author jmunoz
	 * @since 12/08/2018
	 * @version 1.0.0
	 * @return List of banks allowed
	 */
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

    /**
     * ping method: Test if service is currently available
     *
     * @author jmunoz
     * @since 12/08/2018
     * @version 1.0.0
     */
    @RequestMapping(value = "/payment/ping", method = RequestMethod.GET)
    public ResponseEntity<Object> ping(HttpServletRequest request, HttpServletResponse response) {

        ResponseEntity responseEntity;
        try {
            boolean pingResponse = businessManager.ping();
            responseEntity =  ResponseEntity.ok(createSuccessResponse(ResponseKeyName.PAYMENT_RESPONSE, pingResponse));
        } catch (HttpClientErrorException ex) {
            responseEntity = setErrorResponse(ex, request);
        }

        return responseEntity;
    }

    /**
     * payService method: perform payment for a service discounting amount from customer balance
     *
     * @param load the whole information necessary to perform money load
     * @author jmunoz
     * @since 12/08/2018
     * @version 1.0.0
     * @return
     */
    @RequestMapping(value = "/payment/service/pay", method = RequestMethod.POST)
    public ResponseEntity<Object> payService(@Validated @RequestBody PaymentDto payment,
                                             BindingResult result, HttpServletRequest request) {
        paymentValidator.validate(payment, result);
        ResponseEntity<Object> responseEntity = apiValidator(result);
        if (responseEntity != null) {
            return responseEntity;
        }

        try {
            boolean paymentResponse = businessManager.payService(payment);
            if (!paymentResponse) {
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(createExistsResponse(
                        ResponseKeyName.PAYMENT_RESPONSE,
                        new HashMap<String, String>().put("message", "already exists")));
            }
            responseEntity =  ResponseEntity.ok(createSuccessResponse(ResponseKeyName.PAYMENT_RESPONSE, paymentResponse));
        } catch (HttpClientErrorException ex) {
            responseEntity = setErrorResponse(ex, request);
        }

        return responseEntity;
    }

    /**
     * getBalance method: get the customer current balance
     *
     * @param customerId
     * @author jmunoz
     * @since 12/08/2018
     * @version 1.0.0
     */
    @RequestMapping(value = "/payment/balance/{customerId}", method = RequestMethod.GET)
    public ResponseEntity<Object> getBalance(@PathVariable("customerId") String customerId, HttpServletRequest request, HttpServletResponse response) {

        ResponseEntity responseEntity;
        try {
            Balance balance = businessManager.getBalance(customerId);
            responseEntity =  ResponseEntity.ok(createSuccessResponse(ResponseKeyName.BALANCE_RESPONSE, balance));
        } catch (HttpClientErrorException ex) {
            responseEntity = setErrorResponse(ex, request);
        }

        return responseEntity;
    }

    /**
     * getService method: get the service payment description
     *
     * @param serviceId
     * @author jmunoz
     * @since 12/08/2018
     * @version 1.0.0
     */
    @RequestMapping(value = "/payment/service/{serviceId}", method = RequestMethod.GET)
    public ResponseEntity<Object> getService(@PathVariable("serviceId") String serviceId, HttpServletRequest request, HttpServletResponse response) {

        ResponseEntity responseEntity;
        try {
            Service service = businessManager.getService(serviceId);
            responseEntity =  ResponseEntity.ok(createSuccessResponse(ResponseKeyName.SERVICE_RESPONSE, service));
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
            case 412:
                map.put("message", "invalid parameter");
                map.put("detail", ex.getMessage());
                status = HttpStatus.PRECONDITION_FAILED;
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
