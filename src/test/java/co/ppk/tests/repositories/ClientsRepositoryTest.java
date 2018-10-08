package co.ppk.tests.repositories;

import co.ppk.data.ClientsRepository;
import co.ppk.domain.Client;
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
import java.util.Optional;

import static co.ppk.tests.TestHelper.getRandomClient;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = BusinessManager.class, properties = { "timezone=GMT", "port=4242" })
public class ClientsRepositoryTest {

    private static DataSource dataSource;
    private static ClientsRepository clientsRepository;

    @BeforeClass
    public static void setUp() throws IOException {
        dataSource = DatasourceSingletonTest.getInstance();
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
    public void testCreateClient() {
        Client newClient = getRandomClient();
        String name = newClient.getName();
        String clientId = clientsRepository.createClient(newClient);
        Optional<Client> client = clientsRepository.getClientById(clientId);
        assertTrue( client.isPresent());
        assertEquals(name, client.get().getName());
    }

    @Test
    public void testGetClientById() {
        Client newClient = getRandomClient();
        String idClient = clientsRepository.createClient(newClient);

        clientsRepository.createClient(getRandomClient());

        Optional<Client> client = clientsRepository.getClientById(idClient);
        assertTrue(client.isPresent());
        assertEquals(idClient, client.get().getId());
    }

    @Test
    public void testgetClientsByStatus() {

        String idClient;
        idClient = clientsRepository.createClient(getRandomClient());
        clientsRepository.updateClientStatus(idClient, Status.SUSPENDED.name());
        idClient = clientsRepository.createClient(getRandomClient());
        clientsRepository.updateClientStatus(idClient, Status.SUSPENDED.name());
        idClient = clientsRepository.createClient(getRandomClient());
        clientsRepository.updateClientStatus(idClient, Status.PENDING.name());
        clientsRepository.createClient(getRandomClient());
        clientsRepository.createClient(getRandomClient());
        clientsRepository.createClient(getRandomClient());

        List<Client> clients = clientsRepository.getClientsByStatus(Status.SUSPENDED.name());
        assertTrue(!clients.isEmpty());
        assertEquals(2, clients.size());

        clients = clientsRepository.getClientsByStatus(Status.PENDING.name());
        assertTrue(!clients.isEmpty());
        assertEquals(1, clients.size());

        clients = clientsRepository.getClientsByStatus(Status.ACTIVE.name());
        assertTrue(!clients.isEmpty());
        assertEquals(3, clients.size());

        clients = clientsRepository.getClientsByStatus(Status.APPROVED.name());
        assertTrue(clients.isEmpty());

        assertEquals(6, clientsRepository.getClients().size());
    }
}
