package com.offmeta.gg.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Apply to both /api/user/** and /api/participant/** routes
        registry.addMapping("/api/**")
                .allowedOrigins("http://timparkaoffmetafe.s3-website-us-east-1.amazonaws.com")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .exposedHeaders("header1", "header2")
                .allowCredentials(true)
                .maxAge(3600);
    }
}

