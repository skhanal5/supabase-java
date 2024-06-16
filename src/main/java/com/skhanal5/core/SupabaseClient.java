package com.skhanal5.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skhanal5.core.query.*;
import lombok.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
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

    public <T> T executeSelect(SelectQuery query, Class<T> responseType) {
        var queryParams = query.convertToQueryParams();
        var additionalHeaders = query.addPaginationHeader();
        return this.makeSelectAPICall(query.getTable(),queryParams, additionalHeaders, responseType);
    }

    public <T> T executeInsert(InsertQuery query, Class<T> responseType) {
        var requestBody = query.getValuesToInsert();
        var additionalHeaders = query.addSelectHeader();
        return this.makeInsertDBCall(query.getTable(), requestBody, additionalHeaders, responseType);
    }

    public <T> T executeUpdate(UpdateQuery query, Class<T> responseType) {
        var requestBody = query.getValuesToUpdate();
        var headers = query.addSelectHeader();
        var queryParams = query.convertToQueryParams();
        return this.makeUpdateDBCall(query.getTable(), headers, queryParams, requestBody, responseType);
    }

    public <T> T executeDelete(DeleteQuery query, Class<T> responseType) {
        var queryParams = query.convertToQueryParams();
        return this.makeDeleteAPICall(query.getTable(), queryParams, responseType);
    }

    private <T> T makeSelectAPICall(String table,
                                    MultiValueMap<String, String> queryParameters,
                                    Consumer<HttpHeaders> headersConsumer,
                                    Class<T> responseType) {
        return client
                .get()
                .uri(uriBuilder ->  uriBuilder
                        .path(table)
                        .queryParams(queryParameters)
                        .build())
                .headers(headersConsumer)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    private <T> T makeInsertDBCall(String table,
                                   List<Map<String, Object>> requestBody, Consumer<HttpHeaders> headersConsumer, Class<T> responseType) {
        return client
                .post()
                .uri(uriBuilder -> {
                    var uri =  uriBuilder
                            .path(table)
                            .build();
                    System.out.println(uri);
                    return uri;
                })
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headersConsumer)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    private <T> T makeUpdateDBCall(String table,
                                   Consumer<HttpHeaders> headers,
                                   MultiValueMap<String, String> queryParameters,
                                   List<Map<String, Object>> requestBody,
                                   Class<T> responseType) {
        return client
                .patch()
                .uri(uriBuilder -> {
                    var uri =  uriBuilder
                            .path(table)
                            .queryParams(queryParameters)
                            .build();
                    System.out.println(uri);
                    return uri;
                })
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    private <T> T makeDeleteAPICall(String table,
                             MultiValueMap<String, String> queryParameters,
                             Class<T> responseType) {
        return client
                .delete()
                .uri(uriBuilder -> {
                    var uri =  uriBuilder
                            .path(table)
                            .queryParams(queryParameters)
                            .build();
                    System.out.println(uri);
                    return uri;
                })
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
