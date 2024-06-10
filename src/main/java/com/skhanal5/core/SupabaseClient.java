package com.skhanal5.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.function.Consumer;


/**
 * Represents a client to interact with the Supabase database.
 */

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

    public <T> T executeQuery(Query query, Class<T> responseType) {
        var queryParams = Query.convertToQueryParams(query);
        var additionalHeaders = Query.buildAdditionalHeaders(query);
        return this.queryDatabaseAPI(query.getTable(),queryParams, additionalHeaders, responseType);
    }

//    public <T> T selectAll(String table, Class<T> responseType) {
//        return this.select(table, "*", responseType);
//    }
//
//    public <T> T select(String table, String column, Class<T> responseType) {
//        var queryParams = CollectionUtils.toMultiValueMap(Map.of("select", List.of(column)));
//        return this.queryDatabaseAPI(table, queryParams, responseType);
//    }
//
//    public <T> T select(String table, List<String> columns, Class<T> responseType) {
//        var queryParams = CollectionUtils.toMultiValueMap(Map.of("select", columns));
//        return this.queryDatabaseAPI(table, queryParams, responseType);
//    }

    private <T> T queryDatabaseAPI(String table, MultiValueMap<String, String> queryParameters,
                                   Consumer<HttpHeaders> headersConsumer, Class<T> responseType) {
        return client
                .get()
                .uri(uriBuilder ->  {
                    var uri = uriBuilder
                            .path(table)
                            .queryParams(queryParameters)
                            .build();
                    System.out.println(uri.toString());
                    return uri;
                })
                .headers(headersConsumer)
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
