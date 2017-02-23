package com.github.tempora;

import org.atmosphere.cpr.SessionSupport;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@ComponentScan
public class TemporaApplication {

	public static void main(String[] args) {
		SpringApplication.run(TemporaApplication.class, args);
	}

	@Bean
	public SessionSupport atmosphereSessionSupport() {
		return new SessionSupport();
	}

}
