package com.skhanal5.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.springframework.web.reactive.function.client.WebClient;

public class SupabaseClient {

    @NonNull
    WebClient client;

    @NonNull
    ObjectMapper mapper;

    private SupabaseClient(WebClient client) {
        this.client = client;
        this.mapper = new ObjectMapper();
    }

    private SupabaseClient(WebClient client, ObjectMapper mapper) {
       this.client = client;
       this.mapper = mapper;
    }

    public <T> T selectAll(String table, Class<T> responseType) {
        return client
                .get()
                .uri(uriBuilder ->  {
                    var uri = uriBuilder
                            .path(table)
                            .queryParam("select", "*")
                            .build();
                    System.out.println(uri.toString());
                    return uri;
                })
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    public static SupabaseClient newInstance(@NonNull String databaseUrl, @NonNull String serviceKey) {
        var client = WebClient
                .builder()
                .baseUrl(databaseUrl)
                .defaultHeader("apikey", serviceKey)
                .defaultHeader("Authorization", "Bearer " + serviceKey)
                .build();

        return new SupabaseClient(client);
    }

    public static SupabaseClient newInstance(@NonNull String databaseUrl, @NonNull String serviceKey, @NonNull ObjectMapper mapper) {
        var client = WebClient
                .builder()
                .baseUrl(databaseUrl)
                .defaultHeader("apikey", serviceKey)
                .defaultHeader("Authorization", "Bearer " + serviceKey)
                .build();

       return new SupabaseClient(client, mapper);
    }

    public static SupabaseClient newInstance(@NonNull WebClient client, @NonNull ObjectMapper mapper) {
        return new SupabaseClient(client, mapper);
    }

    public static SupabaseClient newInstance(@NonNull WebClient client) {
        return new SupabaseClient(client);
    }

}
