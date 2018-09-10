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

import co.ppk.service.BusinessManager;
import co.ppk.service.impl.BussinessManagerImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import co.ppk.config.ApplicationConfig;

import static co.ppk.utilities.Constants.CHECK_PAYMENTS_STATUS_INTERVAL;

/***
 * Configuration class for Spring IOC
 * 
 * @Descripcion
 * @author jmunoz
 * 
 * @version 1.0
 */

@SpringBootApplication
@Import({ApplicationConfig.class})
public class SpringBootController  {
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        System.out.println("Starting to check pending payments 0...");
        System.setProperty("PPK_HOME", "/ppk");
        SpringApplication.run(SpringBootController.class, args);
        final long timeInterval = CHECK_PAYMENTS_STATUS_INTERVAL * 60000;
        Runnable runnable = new Runnable() {
            BusinessManager businessManager = new BussinessManagerImpl();
            public void run() {
                while (true) {
                    System.out.println("Starting to check pending payments...");
                    businessManager.checkPendingPayments();
                    try {
                        Thread.sleep(timeInterval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

}
