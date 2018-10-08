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
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import co.ppk.domain.Balance;
import co.ppk.domain.Load;
import co.ppk.domain.Service;
import co.ppk.dto.ApiKeyDto;
import co.ppk.dto.ClientDto;
import co.ppk.dto.LoadRequestDto;
import co.ppk.dto.PaymentDto;
import co.ppk.enums.Country;
import co.ppk.enums.CreditCardType;
import co.ppk.service.BusinessManager;
import co.ppk.service.MeatadataBO;
import co.ppk.validators.ApiKeyValidator;
import co.ppk.validators.ClientValidator;
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

import static co.ppk.utilities.Constants.CURRENT_CLIENT_KEY_HEADER;
import static co.ppk.utilities.PaymentGatewayHelper.getMetadata;
import static co.ppk.utilities.KeyHelper.validateKey;

/**
 * Only service exposition point of services to FE layer
 * 
 * @author jmunoz
 * @version 1.0.0
 * @since 08/09/2018
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

    @Autowired
    ClientValidator clientValidator;

    @Autowired
    ApiKeyValidator apiKeyValidator;

	/**
	 * loadPayment method: perform a new money load for an specific customer
	 *
	 * @param load the whole information necessary to perform money load
	 * @author jmunoz
	 * @since 12/08/2018
	 * @return load confirmation registry
	 */
    @RequestMapping(value = "/payment/load", method = RequestMethod.POST)
    public ResponseEntity<Object> loadPayment(@RequestHeader(required = false, value = CURRENT_CLIENT_KEY_HEADER) String key,
											  @Validated @RequestBody LoadRequestDto load,
                                                 BindingResult result, HttpServletRequest request) {
		loadRequestValidator.validate(load, result);
		ResponseEntity<Object> responseEntity = apiValidator(result);
		if (responseEntity != null) {
			return responseEntity;
		}

		try {
            validateKey(key);
            MeatadataBO metadata = getMetadata(request);
            Load registry = businessManager.loadPayment(load, metadata, key);
			responseEntity =  ResponseEntity.ok(createSuccessResponse(ResponseKeyName.PAYMENT_RESPONSE, registry));
		} catch (HttpClientErrorException ex) {
			responseEntity = setErrorResponse(ex, request);
		} catch (NoSuchAlgorithmException ex) {
			responseEntity = setErrorResponse(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR), request);
		} catch (ParseException e) {
			responseEntity = setErrorResponse(new HttpClientErrorException(HttpStatus.UNAUTHORIZED), request);
		}

		return responseEntity;
    }

	/**
	 * getBanks method: Retrieve the list of banks supported by application to make wire transfers
	 *
	 * @param country the country where are based banks list
	 * @author jmunoz
	 * @since 12/08/2018
	 * @return List of banks allowed
	 */
	@RequestMapping(value = "/payment/cash/banks/{country}", method = RequestMethod.GET)
	public ResponseEntity<Object> getBanks(@RequestHeader(required = false, value = CURRENT_CLIENT_KEY_HEADER) String key,
										   @PathVariable("country") String country, HttpServletRequest request, HttpServletResponse response) {

		ResponseEntity<Object> responseEntity;
    	try {
            validateKey(key);
    	    List<com.payu.sdk.model.Bank> banks = businessManager.getBanks(Country.valueOf(country.toUpperCase()), key);
			responseEntity =  ResponseEntity.ok(createSuccessResponse(ResponseKeyName.PAYMENT_RESPONSE, banks));
		} catch (HttpClientErrorException ex) {
			responseEntity = setErrorResponse(ex, request);
		} catch (IllegalArgumentException ex) {
			responseEntity = setErrorResponse(new HttpClientErrorException(HttpStatus.NOT_FOUND), request);
		} catch (ParseException e) {
			responseEntity = setErrorResponse(new HttpClientErrorException(HttpStatus.UNAUTHORIZED), request);
		}

		return responseEntity;
	}

    /**
     * ping method: Test if service is currently available
     *
     * @author jmunoz
     * @since 12/08/2018
	 * @return service availability response
     */
    @RequestMapping(value = "/payment/ping", method = RequestMethod.GET)
    public ResponseEntity<Object> ping(@RequestHeader(required = false, value = CURRENT_CLIENT_KEY_HEADER) String key,
                                       HttpServletRequest request, HttpServletResponse response) {

        ResponseEntity<Object> responseEntity;
        try {
			validateKey(key);
            boolean pingResponse = businessManager.ping(key);
            responseEntity =  ResponseEntity.ok(createSuccessResponse(ResponseKeyName.PAYMENT_RESPONSE, pingResponse));
        } catch (HttpClientErrorException ex) {
            responseEntity = setErrorResponse(ex, request);
        } catch (ParseException e) {
			responseEntity = setErrorResponse(new HttpClientErrorException(HttpStatus.UNAUTHORIZED), request);
		}

		return responseEntity;
    }

    /**
     * payService method: perform payment for a service discounting amount from customer balance
     *
     * @param payment the whole information necessary to perform service payment
     * @author jmunoz
     * @since 12/08/2018
     * @return payment response
     */
    @RequestMapping(value = "/payment/service/pay", method = RequestMethod.POST)
    public ResponseEntity<Object> payService(@RequestHeader(required = false, value = CURRENT_CLIENT_KEY_HEADER) String key,
											 @Validated @RequestBody PaymentDto payment,
                                             BindingResult result, HttpServletRequest request) {
        paymentValidator.validate(payment, result);
        ResponseEntity<Object> responseEntity = apiValidator(result);
        if (responseEntity != null) {
            return responseEntity;
        }

        try {
			validateKey(key);
        	boolean paymentResponse = businessManager.payService(payment, key);
            if (!paymentResponse) {
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(createExistsResponse(
                        ResponseKeyName.PAYMENT_RESPONSE,
                        new HashMap<String, String>().put("message", "already exists")));
            }
            responseEntity =  ResponseEntity.ok(createSuccessResponse(ResponseKeyName.PAYMENT_RESPONSE, paymentResponse));
        } catch (HttpClientErrorException ex) {
            responseEntity = setErrorResponse(ex, request);
        } catch (ParseException e) {
			responseEntity = setErrorResponse(new HttpClientErrorException(HttpStatus.UNAUTHORIZED), request);
		}

		return responseEntity;
    }

    /**
     * getBalance method: get the customer current balance
     *
     * @param customerId Customer universal identifier
     * @author jmunoz
     * @since 12/08/2018
	 * @return balance of given customer
     */
    @RequestMapping(value = "/payment/balance/{customerId}", method = RequestMethod.GET)
    public ResponseEntity<Object> getBalance(@RequestHeader(required = false, value = CURRENT_CLIENT_KEY_HEADER) String key,
											 @PathVariable("customerId") String customerId, HttpServletRequest request, HttpServletResponse response) {

        ResponseEntity<Object> responseEntity;
        try {
			validateKey(key);
            Balance balance = businessManager.getBalance(customerId, key);
            responseEntity =  ResponseEntity.ok(createSuccessResponse(ResponseKeyName.BALANCE_RESPONSE, balance));
        } catch (HttpClientErrorException ex) {
            responseEntity = setErrorResponse(ex, request);
        } catch (ParseException e) {
			responseEntity = setErrorResponse(new HttpClientErrorException(HttpStatus.UNAUTHORIZED), request);
		}

		return responseEntity;
    }

    /**
     * getServiceById method: get the service payment description
     *
     * @param serviceId service universal identifier
     * @author jmunoz
     * @since 12/08/2018
	 * @return all data related with the given service id
     */
    @RequestMapping(value = "/payment/service/{serviceId}", method = RequestMethod.GET)
    public ResponseEntity<Object> getService(@RequestHeader(required = false, value = CURRENT_CLIENT_KEY_HEADER) String key,
											 @PathVariable("serviceId") String serviceId, HttpServletRequest request,
											 HttpServletResponse response) {

        ResponseEntity<Object> responseEntity;
        try {
			validateKey(key);
            Service service = businessManager.getService(serviceId, key);
            responseEntity =  ResponseEntity.ok(createSuccessResponse(ResponseKeyName.SERVICE_RESPONSE, service));
        } catch (HttpClientErrorException ex) {
            responseEntity = setErrorResponse(ex, request);
        } catch (ParseException e) {
			responseEntity = setErrorResponse(new HttpClientErrorException(HttpStatus.UNAUTHORIZED), request);
		}

		return responseEntity;
    }

	/**
	 * getCredicardTypes method: get the list of allowed credit card types
	 *
	 * @author jmunoz
	 * @since 12/08/2018
	 * @return allowed credit card types
	 */
	@RequestMapping(value = "/payment/credit-cards", method = RequestMethod.GET)
	public ResponseEntity<Object> getCredicardTypes(HttpServletRequest request, HttpServletResponse response) {
		ResponseEntity<Object> responseEntity;
		try {
			List<CreditCardType> types = businessManager.getCreditCardTypes();
			responseEntity =  ResponseEntity.ok(createSuccessResponse(ResponseKeyName.SERVICE_RESPONSE, types));
		} catch (HttpClientErrorException ex) {
			responseEntity = setErrorResponse(ex, request);
		}

		return responseEntity;
	}

    /**
     * createClient method: Create a new client for use payments platform
     *
     * @author jmunoz
     * @since 12/08/2018
     * @return clientId json object
     */
    @RequestMapping(value = "/client", method = RequestMethod.POST)
    public ResponseEntity<Object> createClient(@Validated @RequestBody ClientDto client,
                                             BindingResult result, HttpServletRequest request) {
        clientValidator.validate(client, result);
        ResponseEntity<Object> responseEntity = apiValidator(result);
        if (responseEntity != null) {
            return responseEntity;
        }

        try {
            String clientId = businessManager.createClient(client);
            if (clientId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(createExistsResponse(
                        ResponseKeyName.PAYMENT_RESPONSE,
                        new HashMap<String, String>().put("message", "already exists")));
            }
            responseEntity =  ResponseEntity.ok(createSuccessResponse(ResponseKeyName.CLIENT_RESPONSE, clientId));
        } catch (HttpClientErrorException ex) {
            responseEntity = setErrorResponse(ex, request);
        }

        return responseEntity;
    }

    /**
     * generateApiKey method: Generate a new valid client api key for use payments platform
     *
     * @author jmunoz
     * @since 30/09/2018
     * @return api key json object
     */
    @RequestMapping(value = "/client/generate-api-key", method = RequestMethod.POST)
    public ResponseEntity<Object> generateApiKey(@Validated @RequestBody ApiKeyDto apiKey,
                                               BindingResult result, HttpServletRequest request) {
        apiKeyValidator.validate(apiKey, result);
        ResponseEntity<Object> responseEntity = apiValidator(result);
        if (responseEntity != null) {
            return responseEntity;
        }

        try {
            String key = businessManager.createApiKey(apiKey);
            responseEntity =  ResponseEntity.ok(createSuccessResponse(ResponseKeyName.API_KEY_RESPONSE, key));
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
