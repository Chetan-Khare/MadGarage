package com.madgarage.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;

@SpringBootApplication
@org.springframework.cache.annotation.EnableCaching
@org.springframework.scheduling.annotation.EnableScheduling
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
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
		// Programmatic override for upload limits (10MB file / 50MB request)
		// This explicitly sets the limits at the Servlet level.
		return new jakarta.servlet.MultipartConfigElement("", 10485760L, 52428800L, 0);
	}
}
