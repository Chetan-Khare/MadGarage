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
        
        // Calculate physical paths to serve dynamic runtime uploads safely on all OS (especially Windows)
        String userDir = System.getProperty("user.dir");
        
        String uploadPath = System.getenv("UPLOAD_DIR") != null 
            ? Paths.get(System.getenv("UPLOAD_DIR"), "uploads").toUri().toString()
            : Paths.get(userDir, "data/uploads").toUri().toString();
            
        String guidesPath = System.getenv("UPLOAD_DIR") != null 
            ? Paths.get(System.getenv("UPLOAD_DIR"), "guides").toUri().toString()
            : Paths.get(userDir, "data/guides").toUri().toString();

        if (!uploadPath.endsWith("/")) {
            uploadPath += "/";
        }
        if (!guidesPath.endsWith("/")) {
            guidesPath += "/";
        }

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath, "classpath:/static/uploads/");

        registry.addResourceHandler("/guides/**")
                .addResourceLocations(guidesPath, "classpath:/static/guides/");

        // Expose the "images" folder (for brand logos)
        // This maps /images/logos/tata.png to src/main/resources/static/images/logos/tata.png
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");
    }

    // CORS configuration is handled entirely by SecurityConfig
}