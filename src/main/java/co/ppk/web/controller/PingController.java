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

import org.apache.coyote.AbstractProtocol;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.concurrent.TimeUnit;

/***
 * Configuration class for Spring IOC
 * 
 * @author jmunoz
 * 
 * @version 1.0
 */
@RestController
@RequestMapping("/test")
public class PingController {

	@RequestMapping(value = "/ping", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> pingService(HttpServletRequest request) {

//		HttpSession session = request.getSession();
//        int connectionTimeout = session.getMaxInactiveInterval();
//        //session.setMaxInactiveInterval(10*60);
//
//		int expectedTimeout = 100500;
//		if (connectionTimeout != expectedTimeout) {
//			throw new IllegalStateException("incorrect connection timeout, expected [" + expectedTimeout + "] but found [" + connectionTimeout + "]");
//		} else {
//			System.out.println("Connection timeout is set as expected to " + expectedTimeout);
//		}


        try {
            for(int i = 0 ; i < 10 ; i++) {
                TimeUnit.MILLISECONDS.sleep(1000);
                System.out.println("Waiting " + i);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok("Backend API is up and running fine!!");
	}
	
}
