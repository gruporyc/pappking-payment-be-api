package co.ppk.tests.repositories;

import co.ppk.data.ClientsRepository;
import co.ppk.data.PaymentsRepository;
import co.ppk.domain.Balance;
import co.ppk.domain.Load;
import co.ppk.domain.Service;
import co.ppk.enums.PaymentMethod;
import co.ppk.enums.Status;
import co.ppk.service.BusinessManager;
import org.flywaydb.core.Flyway;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static co.ppk.tests.TestHelper.getRandomClient;
import static co.ppk.tests.TestHelper.getRandomLoad;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = BusinessManager.class, properties = { "timezone=GMT", "port=4242" })
public class PaymentsRepositoryTest {

    private static DataSource dataSource;
    private static PaymentsRepository paymentsRepository;
    private static ClientsRepository clientsRepository;

    private static final String TEST_NETWORK_CODE = "NETWORK_CODE_ABCDEabcde123456";
    private static final String TEST_ORDER_ID = "ORDER_ID_ABCDEabcde123456";
    private static final String TEST_TRANSACTION_ID = "TRANSACTION_ID_ABCDEabcde123456";
    private static final String TEST_TRAZABILITY_CODE = "TRASABILITY_CODE_ABCDEabcde123456";
    private static final String TEST_RESPONSE_CODE = "RESPONSE_CODE_ABCDEabcde123456";
    private static final String TEST_NETWORK_MESSAGE = "Test network";
    private static final double TEST_AMOUNT = 12345.67;
    private static final double TEST_SERVICE_AMOUNT = 8000;
    private static final double DELTA = 0.5;


    @BeforeClass
    public static void setUp() throws IOException {
        dataSource = DatasourceSingletonTest.getInstance();
        clientsRepository = new ClientsRepository(dataSource);
        paymentsRepository = new PaymentsRepository(dataSource);

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
    public void testCreateLoad() {
        String clientId = clientsRepository.createClient(getRandomClient());
        String loadId = paymentsRepository.createLoad(getRandomLoad(clientId, UUID.randomUUID().toString(), PaymentMethod.CREDIT.name()));

        assertTrue(Objects.nonNull(loadId) && !loadId.isEmpty());
    }

    @Test
    public void testUpdateLoad() {
        String clientId = clientsRepository.createClient(getRandomClient());

        String customerId = UUID.randomUUID().toString();
        Load newLoad = getRandomLoad(clientId, customerId, PaymentMethod.CREDIT.name());
        String loadId = paymentsRepository.createLoad(newLoad);

        paymentsRepository.updateLoad(new Load.Builder()
                .setAmount(TEST_AMOUNT)
                .setCurrency(newLoad.getCurrency())
                .setCustomerId(newLoad.getCustomerId())
                .setMethod(newLoad.getMethod())
                .setStatus(Status.APPROVED.name())
                .setCountry(newLoad.getCountry())
                .setId(loadId)
                .setClientId(clientId)
                .setNetworkCode(TEST_NETWORK_CODE)
                .setNetworkMessage(TEST_NETWORK_MESSAGE)
                .setOrderId(TEST_ORDER_ID)
                .setResponseCode(TEST_RESPONSE_CODE)
                .setTransactionId(TEST_TRANSACTION_ID)
                .setTrazabilityCode(TEST_TRAZABILITY_CODE)
                .build());

        Optional<Load> load = paymentsRepository.getLoadById(loadId);

        assertTrue(load.isPresent());
        assertEquals(clientId, load.get().getClientId());
        assertEquals(Status.APPROVED.name(), load.get().getStatus());
        assertEquals(TEST_NETWORK_CODE, load.get().getNetworkCode());
        assertEquals(TEST_NETWORK_MESSAGE, load.get().getNetworkMessage());
        assertEquals(TEST_ORDER_ID, load.get().getOrderId());
        assertEquals(TEST_RESPONSE_CODE, load.get().getResponseCode());
        assertEquals(TEST_TRANSACTION_ID, load.get().getTransactionId());
        assertEquals(TEST_TRAZABILITY_CODE, load.get().getTrazabilityCode());

        Optional<Balance> balance = paymentsRepository.getBalance(customerId, clientId);

        assertTrue(balance.isPresent());
        assertEquals(TEST_AMOUNT, balance.get().getBalance(), DELTA);

        loadId = paymentsRepository.createLoad(newLoad);

        paymentsRepository.updateLoad(new Load.Builder()
                .setAmount(TEST_AMOUNT)
                .setCurrency(newLoad.getCurrency())
                .setCustomerId(newLoad.getCustomerId())
                .setMethod(newLoad.getMethod())
                .setStatus(Status.APPROVED.name())
                .setCountry(newLoad.getCountry())
                .setId(loadId)
                .setClientId(clientId)
                .setNetworkCode(TEST_NETWORK_CODE)
                .setNetworkMessage(TEST_NETWORK_MESSAGE)
                .setOrderId(TEST_ORDER_ID)
                .setResponseCode(TEST_RESPONSE_CODE)
                .setTransactionId(TEST_TRANSACTION_ID)
                .setTrazabilityCode(TEST_TRAZABILITY_CODE)
                .build());

        balance = paymentsRepository.getBalance(customerId, clientId);

        assertTrue(balance.isPresent());
        assertEquals(TEST_AMOUNT * 2, balance.get().getBalance(), DELTA);
    }

    @Test
    public void testUpdateLoadStatus() {

        String clientId = clientsRepository.createClient(getRandomClient());

        Load newLoad = getRandomLoad(clientId, UUID.randomUUID().toString(), PaymentMethod.CREDIT.name());
        String loadId = paymentsRepository.createLoad(newLoad);

        paymentsRepository.updateLoadStatus(loadId, clientId, Status.APPROVED.name());

        Optional<Load> load = paymentsRepository.getLoadById(loadId);

        assertTrue(load.isPresent());
        assertEquals(Status.APPROVED.name(), load.get().getStatus());
    }

    @Test
    public void testGetLoadsByStatus() {

        String clientId = clientsRepository.createClient(getRandomClient());

        Load newLoad = getRandomLoad(clientId, UUID.randomUUID().toString(), PaymentMethod.CREDIT.name());
        String loadId = paymentsRepository.createLoad(newLoad);

        List<Load> loads = paymentsRepository.getLoadsByStatus(Status.INCOMPLETE.name(), clientId);
        assertTrue(!loads.isEmpty());
        String loadIdExists = "";
        for (Load load : loads) {
            loadIdExists = load.getId().equals(loadId) ? load.getId() : "";
            assertEquals(Status.INCOMPLETE.name(), load.getStatus());
        }
        assertEquals(loadId, loadIdExists);
    }

    @Test
    public void testGetLoadsById() {

        String clientId = clientsRepository.createClient(getRandomClient());

        Load newLoad = getRandomLoad(clientId, UUID.randomUUID().toString(), PaymentMethod.CREDIT.name());
        String loadId = paymentsRepository.createLoad(newLoad);

        Optional<Load> load = paymentsRepository.getLoadById(loadId);
        assertTrue(load.isPresent());

        assertEquals(loadId, load.get().getId());
    }

    @Test
    public void testGetService() {

        String clientId = clientsRepository.createClient(getRandomClient());

        Load newLoad = getRandomLoad(clientId, UUID.randomUUID().toString(), PaymentMethod.CREDIT.name());
        String loadId = paymentsRepository.createLoad(newLoad);

        Optional<Load> load = paymentsRepository.getLoadById(loadId);
        assertTrue(load.isPresent());

        assertEquals(loadId, load.get().getId());
    }

    @Test
    public void testCreateServicePayment() {

        String clientId = clientsRepository.createClient(getRandomClient());

        String customerId = UUID.randomUUID().toString();
        Load newLoad = getRandomLoad(clientId, customerId, PaymentMethod.CREDIT.name());
        String loadId = paymentsRepository.createLoad(newLoad);

        paymentsRepository.updateLoad(new Load.Builder()
                .setAmount(TEST_AMOUNT)
                .setCurrency(newLoad.getCurrency())
                .setCustomerId(newLoad.getCustomerId())
                .setMethod(newLoad.getMethod())
                .setStatus(Status.APPROVED.name())
                .setCountry(newLoad.getCountry())
                .setId(loadId)
                .setClientId(clientId)
                .setNetworkCode(TEST_NETWORK_CODE)
                .setNetworkMessage(TEST_NETWORK_MESSAGE)
                .setOrderId(TEST_ORDER_ID)
                .setResponseCode(TEST_RESPONSE_CODE)
                .setTransactionId(TEST_TRANSACTION_ID)
                .setTrazabilityCode(TEST_TRAZABILITY_CODE)
                .build());

        String legacyServiceId = UUID.randomUUID().toString();

        paymentsRepository.createServicePayment(new Service.Builder()
                .setCustomerId(customerId)
                .setServiceId(legacyServiceId)
                .setClientId(clientId)
                .setAmount(TEST_SERVICE_AMOUNT)
                .build());

        Optional<Service> service = paymentsRepository.getServiceById(legacyServiceId, clientId);

        assertTrue(service.isPresent());
        assertEquals(TEST_SERVICE_AMOUNT, service.get().getAmount(), DELTA);

        Optional<Balance> newBalance = paymentsRepository.getBalance(customerId, clientId);

        assertTrue(newBalance.isPresent());
        assertEquals(TEST_AMOUNT - TEST_SERVICE_AMOUNT, newBalance.get().getBalance(), DELTA);
    }

    @Test
    public void testGetBalance() {

        String clientId = clientsRepository.createClient(getRandomClient());

        String customerId = UUID.randomUUID().toString();
        Load newLoad = getRandomLoad(clientId, customerId, PaymentMethod.CREDIT.name());
        String loadId = paymentsRepository.createLoad(newLoad);

        paymentsRepository.updateLoad(new Load.Builder()
                .setAmount(TEST_AMOUNT)
                .setCurrency(newLoad.getCurrency())
                .setCustomerId(newLoad.getCustomerId())
                .setMethod(newLoad.getMethod())
                .setStatus(Status.APPROVED.name())
                .setCountry(newLoad.getCountry())
                .setId(loadId)
                .setClientId(clientId)
                .setNetworkCode(TEST_NETWORK_CODE)
                .setNetworkMessage(TEST_NETWORK_MESSAGE)
                .setOrderId(TEST_ORDER_ID)
                .setResponseCode(TEST_RESPONSE_CODE)
                .setTransactionId(TEST_TRANSACTION_ID)
                .setTrazabilityCode(TEST_TRAZABILITY_CODE)
                .build());

        Optional<Balance> balance = paymentsRepository.getBalance(customerId, clientId);

        assertTrue(balance.isPresent());
        assertEquals(TEST_AMOUNT, balance.get().getBalance(), DELTA);
    }
}

