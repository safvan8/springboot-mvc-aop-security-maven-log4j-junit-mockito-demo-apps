package com.safvan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AppConfig {

	@Bean("bcrypt")
	public BCryptPasswordEncoder createBCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
