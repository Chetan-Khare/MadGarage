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
            : Paths.get(userDir, "src/main/resources/static/uploads").toUri().toString();
            
        String guidesPath = System.getenv("UPLOAD_DIR") != null 
            ? Paths.get(System.getenv("UPLOAD_DIR"), "guides").toUri().toString()
            : Paths.get(userDir, "src/main/resources/static/guides").toUri().toString();

        // Expose the "uploads" folder using physical file system URI (allows runtime uploads to be served)
        // Ensure trailing slash is present for Spring resource locations
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath.endsWith("/") ? uploadPath : uploadPath + "/");

        // Expose the "guides" folder
        registry.addResourceHandler("/guides/**")
                .addResourceLocations(guidesPath.endsWith("/") ? guidesPath : guidesPath + "/");

        // Expose the "images" folder (for brand logos)
        // This maps /images/logos/tata.png to src/main/resources/static/images/logos/tata.png
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");
    }

    // CORS configuration is handled entirely by SecurityConfig
}