package com.goldloan.safegold1.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Handle images from the static/images directory
        String uploadPath = Paths.get("src/main/resources/static/images").toAbsolutePath().toString();
        
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/", "file:" + uploadPath + "/")
                .setCachePeriod(3600);
        
        // Handle other static resources
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600);
    }
}



