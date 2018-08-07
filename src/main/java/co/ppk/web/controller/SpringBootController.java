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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import co.ppk.config.ApplicationConfig;

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
		System.setProperty("PPK_HOME", "/ppk");
		SpringApplication.run(SpringBootController.class, args);
	}

}
