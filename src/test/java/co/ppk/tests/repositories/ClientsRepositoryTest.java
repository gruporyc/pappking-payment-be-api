package co.ppk.tests.repositories;

import co.ppk.service.BusinessManager;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = BusinessManager.class, properties = { "timezone=GMT", "port=4242" })
public class ClientsRepositoryTest {

    @Before
    public void setUp() {

    }
}
