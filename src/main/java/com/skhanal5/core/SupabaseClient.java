package com.skhanal5.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.springframework.web.reactive.function.client.WebClient;


@Value
public class SupabaseClient<T> {
    WebClient client;

    ObjectMapper mapper;

    Class<T> responseType;

    private SupabaseClient(ObjectMapper mapper, String serviceKey, @NonNull String databaseUrl, Class<T> responseType) {
        this.client = WebClient.builder()
                .baseUrl(databaseUrl)
                .defaultHeader("apikey", serviceKey)
                .defaultHeader("Authorization", serviceKey)
                .build();
        this.mapper = mapper;
        this.responseType = responseType;
    }


    public static <T>  SupabaseClient<T> newInstance(ObjectMapper mapper,
                                                String serviceKey,
                                                String databaseUrl,
                                                Class<T> responseType) {
       return new SupabaseClient<>(mapper, serviceKey, databaseUrl, responseType);

    }

}
