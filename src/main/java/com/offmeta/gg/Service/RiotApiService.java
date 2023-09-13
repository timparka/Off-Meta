package com.offmeta.gg.Service;

import no.stelar7.api.r4j.basic.APICredentials;
import no.stelar7.api.r4j.impl.R4J;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RiotApiService {

    private final R4J api;

    public RiotApiService(@Value("${api.key}") String apiKey) {
        this.api = new R4J(new APICredentials(apiKey));
    }

    public R4J getApi() {
        return api;
    }
}