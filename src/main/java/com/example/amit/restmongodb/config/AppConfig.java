package com.example.amit.restmongodb.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
	@Bean
	public FilterRegistrationBean<PetHTTPFilter> filterRegistrationBean() {
		FilterRegistrationBean<PetHTTPFilter> registrationBean = new FilterRegistrationBean();
		PetHTTPFilter customURLFilter = new PetHTTPFilter();

		registrationBean.setFilter(customURLFilter);
		registrationBean.addUrlPatterns("/pets/create");
		registrationBean.setOrder(1); // set precedence
		return registrationBean;
	}

}
