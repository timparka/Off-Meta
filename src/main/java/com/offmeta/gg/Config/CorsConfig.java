package com.offmeta.gg.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Specific origins you want to allow
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://timparkaoffmetafe.s3-website-us-east-1.amazonaws.com");

        // Allowed headers and methods
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        source.registerCorsConfiguration("/api/user/**", config);
        source.registerCorsConfiguration("/api/participant/**", config);

        return new CorsFilter(source);
    }
}
