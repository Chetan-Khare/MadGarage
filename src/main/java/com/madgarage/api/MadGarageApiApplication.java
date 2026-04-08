package com.madgarage.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MadGarageApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MadGarageApiApplication.class, args);
	}
	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}

	@Bean
	public jakarta.servlet.MultipartConfigElement multipartConfigElement() {
		// Programmatic override for upload limits (100MB file / 500MB request)
		// This explicitly sets the limits at the Servlet level.
		return new jakarta.servlet.MultipartConfigElement("", 104857600L, 524288000L, 0);
	}
}
