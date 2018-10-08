package co.ppk.utilities;

import co.ppk.data.ApiKeysRepository;
import co.ppk.data.ClientsRepository;
import co.ppk.data.DataSourceSingleton;
import co.ppk.domain.ApiKey;
import co.ppk.domain.Client;
import co.ppk.enums.Status;
import co.ppk.web.controller.ProxyEndpointController;
import com.payu.sdk.PayU;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import javax.websocket.Session;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import static co.ppk.utilities.Constants.PAYU_IS_TEST;
import static co.ppk.utilities.Constants.PAYU_PAIMENTS_URL;
import static co.ppk.utilities.Constants.PAYU_REPORTS_URL;

public class KeyHelper {

    private static final Logger LOGGER = LogManager.getLogger(ProxyEndpointController.class);

    public static void validateKey(String key) throws ParseException {
        ApiKeysRepository apiKeysRepository = new ApiKeysRepository(DataSourceSingleton.getInstance());
        ClientsRepository clientsRepository = new ClientsRepository(DataSourceSingleton.getInstance());
        Optional<ApiKey> apiKey = apiKeysRepository.getApiKeyById(key);

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        Date expirationDate = format.parse(apiKey.get().getExpirationDate());

        if (!apiKey.isPresent() || !apiKey.get().getStatus().equals(Status.ACTIVE)) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }

        if (expirationDate.after(new Date())) {
            LOGGER.error("Payments key " + apiKey.get().getId() + " expired");

            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }

        Optional<Client> client = clientsRepository.getClientById(apiKey.get().getClientId());
        if (!client.isPresent()) { throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED); }



    }

    public static String loadGatewayKeys(String key) {
        ApiKeysRepository apiKeysRepository = new ApiKeysRepository(DataSourceSingleton.getInstance());
        ClientsRepository clientsRepository = new ClientsRepository(DataSourceSingleton.getInstance());
        Optional<ApiKey> apiKey = apiKeysRepository.getApiKeyById(key);
        if (!apiKey.isPresent()) { throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED); }
        Optional<Client> client = clientsRepository.getClientById(apiKey.get().getClientId());
        if (!client.isPresent()) { throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED); }

        PayU.paymentsUrl = PAYU_PAIMENTS_URL;
        PayU.reportsUrl = PAYU_REPORTS_URL;
        PayU.apiKey = client.get().getGatewayApiKey();
        PayU.apiLogin = client.get().getGatewayApiLogin();
        PayU.merchantId = client.get().getGatewayMerchantId();
        PayU.isTest = PAYU_IS_TEST;
        return client.get().getId();
    }
}
