package co.ppk.tests.repositories;

import co.ppk.data.ApiKeysRepository;
import co.ppk.data.ClientsRepository;
import co.ppk.domain.ApiKey;
import co.ppk.enums.Status;
import co.ppk.service.BusinessManager;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.flywaydb.core.Flyway;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

import static co.ppk.tests.TestHelper.getRandomClient;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = BusinessManager.class, properties = { "timezone=GMT", "port=4242" })
public class ApiKeysRepositoryTest {
    private static DataSource dataSource;
    private static ApiKeysRepository apiKeysRepository;
    private static ClientsRepository clientsRepository;

    @BeforeClass
    public static void setUp() throws IOException {
        dataSource = DatasourceSingletonTest.getInstance();
        apiKeysRepository = new ApiKeysRepository(dataSource);
        clientsRepository = new ClientsRepository(dataSource);

        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.setLocations("classpath:db/migration/payment");
        flyway.setTable("clients_migrations");
        flyway.clean();
        flyway.migrate();
    }

    @AfterClass
    public static void tearDown() throws SQLException {
        dataSource.getConnection().close();
    }

    @Test
    public void testCreateApiKey() {
        String clientId = clientsRepository.createClient(getRandomClient());
        Date now = new Date();
        Date expDate = DateUtils.addDays(now, 180);
        String token = RandomStringUtils.randomAlphanumeric(1024);
        apiKeysRepository.createApiKey(token, clientId, new Timestamp(expDate.getTime()));

        Optional<ApiKey> apiKey = apiKeysRepository.getApiKeyById(token);

        assertTrue(apiKey.isPresent());
        assertEquals(token, apiKey.get().getId());
    }

    @Test
    public void testUpdateApiKeyStatus() {
        String clientId = clientsRepository.createClient(getRandomClient());
        Date now = new Date();
        Date expDate = DateUtils.addDays(now, 180);
        String token = RandomStringUtils.randomAlphanumeric(1024);
        apiKeysRepository.createApiKey(token, clientId, new Timestamp(expDate.getTime()));

        apiKeysRepository.updateApiKeyStatus(token, Status.EXPIRED.name());

        Optional<ApiKey> apiKey = apiKeysRepository.getApiKeyById(token);

        assertTrue(apiKey.isPresent());
        assertEquals(Status.EXPIRED.name(), apiKey.get().getStatus());
    }
}
