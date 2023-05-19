package com.offmeta.gg.Config;

import no.stelar7.api.r4j.basic.APICredentials;
import no.stelar7.api.r4j.impl.R4J;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class R4JConfig {
    @Value("${api.key}")
    private String apiKey;

    @Bean
    public R4J r4j() {
        // Use APICredentials with your API key
        return new R4J(new APICredentials(apiKey));
    }
}
