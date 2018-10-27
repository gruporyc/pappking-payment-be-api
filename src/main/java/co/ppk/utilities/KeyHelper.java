package co.ppk.utilities;

import co.ppk.data.ApiKeysRepository;
import co.ppk.data.ClientsRepository;
import co.ppk.domain.ApiKey;
import co.ppk.domain.Client;
import co.ppk.enums.Status;
import co.ppk.web.controller.ProxyEndpointController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;


public class KeyHelper {

    private static final Logger LOGGER = LogManager.getLogger(ProxyEndpointController.class);

    public static void validateKey(String key) throws ParseException {
        ApiKeysRepository apiKeysRepository = new ApiKeysRepository();
        ClientsRepository clientsRepository = new ClientsRepository();
        Optional<ApiKey> apiKey = apiKeysRepository.getApiKeyByToken(key);

        if (!apiKey.isPresent() || !apiKey.get().getStatus().equals(Status.ACTIVE.name())) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        Date expirationDate = format.parse(apiKey.get().getExpirationDate());

        if (expirationDate.getTime() < new Date().getTime()) {
            LOGGER.error("Payments key " + apiKey.get().getToken() + " expired");

            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }

        Optional<Client> client = clientsRepository.getClientById(apiKey.get().getClientId());
        if (!client.isPresent()) { throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED); }

    }
}
