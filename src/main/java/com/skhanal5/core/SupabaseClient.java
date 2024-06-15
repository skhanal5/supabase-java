package com.skhanal5.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.function.Consumer;


/**
 * Represents a client to interact with the Supabase database.
 */

public class SupabaseClient {

    @NonNull
    WebClient client;

    @NonNull
    ObjectMapper mapper;

    private static final String ENDPOINT_PATH = "/rest/v1/";

    private SupabaseClient(WebClient client) {
        this.client = client;
        this.mapper = new ObjectMapper();
    }

    private SupabaseClient(WebClient client, ObjectMapper mapper) {
       this.client = client;
       this.mapper = mapper;
    }

    public <T> T executeQuery(Query query, Class<T> responseType) {
        var queryParams = Query.convertToQueryParams(query);
        var additionalHeaders = Query.buildAdditionalHeaders(query);
        return this.queryDatabaseAPI(query.getTable(),queryParams, additionalHeaders, responseType);
    }

    private <T> T queryDatabaseAPI(String table, MultiValueMap<String, String> queryParameters,
                                   Consumer<HttpHeaders> headersConsumer, Class<T> responseType) {
        return client
                .get()
                .uri(uriBuilder -> {
                   var uri =  uriBuilder
                            .path(table)
                            .queryParams(queryParameters)
                            .build();
                   System.out.println(uri);
                   return uri;
                })
                .headers(headersConsumer)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    public static SupabaseClient newInstance(@NonNull String databaseUrl, @NonNull String serviceKey) {
        var baseUrl = databaseUrl + ENDPOINT_PATH;
        var client = WebClient
                .builder()
                .baseUrl(baseUrl)
                .defaultHeader("apikey", serviceKey)
                .defaultHeader("Authorization", "Bearer " + serviceKey)
                .build();

        return new SupabaseClient(client);
    }

    public static SupabaseClient newInstance(@NonNull String databaseUrl, @NonNull String serviceKey, @NonNull ObjectMapper mapper) {
        var baseUrl = databaseUrl + ENDPOINT_PATH;
        var client = WebClient
                .builder()
                .baseUrl(baseUrl)
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
