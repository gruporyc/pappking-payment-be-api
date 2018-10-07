package co.ppk.tests;

import co.ppk.service.BusinessManager;
import co.ppk.web.controller.SpringBootController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = BusinessManager.class, properties = { "timezone=GMT", "port=4242" })
public class GeneralTests {

    public GeneralTests() {
    }

    @Before
    public void setUp() {

    }

    @Test
    public void testMainRunner() throws Exception {
        SpringBootController.main(new String[] {});
    }

}
