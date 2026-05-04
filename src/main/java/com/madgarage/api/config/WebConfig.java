package com.madgarage.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Use classpath-based resource locations for better portability across dev/prod environments
        
        // Expose the "uploads" folder (Assuming this might be external in production, but keeping classpath for now)
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("classpath:/static/uploads/");

        // Expose the "guides" folder
        registry.addResourceHandler("/guides/**")
                .addResourceLocations("classpath:/static/guides/");

        // Expose the "images" folder (for brand logos)
        // This maps /images/logos/tata.png to src/main/resources/static/images/logos/tata.png
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");
    }

    // CORS configuration is handled entirely by SecurityConfig
}