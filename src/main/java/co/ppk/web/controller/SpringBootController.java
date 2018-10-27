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

import co.ppk.service.CheckPaymentsService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

import co.ppk.config.ApplicationConfig;


/***
 * Configuration class for Spring IOC
 *
 * @author jmunoz
 * 
 * @version 1.0
 */

@SpringBootApplication
@Import({ApplicationConfig.class})
public class SpringBootController {
    /**
     * @param args application arguments
     */
    public static void main(String[] args) {
        System.setProperty("PPK_HOME", "/ppk");
        ApplicationContext ctx = SpringApplication.run(SpringBootController.class, args);
        Thread subscriber = new Thread(ctx.getBean(CheckPaymentsService.class));
        subscriber.start();
    }

}
