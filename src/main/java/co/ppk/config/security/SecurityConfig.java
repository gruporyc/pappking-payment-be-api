package co.ppk.config.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

/***
 * Main security configuration files where resides the authorization rules and
 * general configuration features
 * 
 * @author Blanclabs
 *
 */
@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private static final Logger LOGGER = LogManager.getLogger(SecurityConfig.class);

	/**
	 * method with authentication rules and general configuration
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.headers().defaultsDisabled().cacheControl().and().httpStrictTransportSecurity().and().contentTypeOptions()
				.and().xssProtection().and().frameOptions();
		
		http.authorizeRequests().mvcMatchers("/v1/message").permitAll().and().csrf().disable();
		http.exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)).and().sessionManagement();
		
		// http.authorizeRequests().mvcMatchers("/v1/login").anonymous().and().authorizeRequests()
		// .antMatchers("/", "/onboarding", "/mortgages", "/statements", "/messages",
		// "/profile", "/reset",
		// "/newDevice", "/scripts/**", "/styles/**", "/fonts/**", "/assets/**",
		// "/favicon.ico")
		// .permitAll().and().authorizeRequests()
		// .mvcMatchers("/api/config/translations", "/api/terms_and_conditions",
		// "/api/onboard", "/api/email",
		// "api/password", "/api/onboard/link/process", "/api/otp/**",
		// "api/customer/credentials/reset",
		// "api/customer/credentials/reset/cmd_proccess", "/api/languages")
		// .permitAll().and().authorizeRequests().mvcMatchers("/api/**").hasAuthority("USER").and()
		// .authorizeRequests().anyRequest().denyAll().and().logout().logoutUrl("api/logout")
		// .clearAuthentication(true).deleteCookies("JSESSIONID").invalidateHttpSession(true);

	}


	/**
	 * method used to activate debug security trace based on logger level
	 * 
	 * @param web
	 * @throws Exception
	 */
	@Override
	public void configure(WebSecurity web) throws Exception {
		if (LOGGER.isDebugEnabled()) {
			web.debug(true);
		}
	}

}
