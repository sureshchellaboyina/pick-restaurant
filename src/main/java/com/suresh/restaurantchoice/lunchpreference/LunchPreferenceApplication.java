package com.suresh.restaurantchoice.lunchpreference;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

//@EnableWebMvc
//@OpenAPIDefinition
@SpringBootApplication
public class LunchPreferenceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LunchPreferenceApplication.class, args);
	}

}
