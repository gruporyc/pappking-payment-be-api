/******************************************************************
 *
 * This code is for the Pappking service project.
 *
 *
 * © 2018, Pappking Management All rights reserved.
 *
 *
 ******************************************************************/

package co.ppk.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import co.ppk.config.ApplicationConfig;

/***
 * Configuration class for Spring IOC
 * 
 * @Descripcion
 * @author jmunoz
 * 
 * @version 1.0
 */
@Configuration
@EnableWebMvc
@Import({ApplicationConfig.class})
public class WebMvcConfig extends WebMvcConfigurationSupport {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport#
	 * addResourceHandlers(org.
	 * springframework.web.servlet.config.annotation.ResourceHandlerRegistry)
	 */
	// equivalents for <mvc:resources/> tags
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

	}


	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport#
	 * configureDefaultServletHandling(org.
	 * springframework.web.servlet.config.annotation.
	 * DefaultServletHandlerConfigurer)
	 */
	// equivalent for <mvc:default-servlet-handler/> tag
	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

}