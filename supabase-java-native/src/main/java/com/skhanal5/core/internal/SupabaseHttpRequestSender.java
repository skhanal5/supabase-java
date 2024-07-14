package com.skhanal5.core.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skhanal5.models.Query;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public class SupabaseHttpRequestSender<T> {

    HttpClient client;

    String table;

    LinkedHashMap<String, List<String>> queryParameters;

    Optional<Map<String, List<String>>> headers;

    ObjectMapper mapper;

    Class<T> responseType;


    public SupabaseHttpRequestSender(HttpClient client, Map<String,String> defaultHeaders, Query query, Class<T> responseType) {
        this.client = client;
        this.table = query.getTable();
        this.queryParameters = query.buildQueryParams(); //combine queryParameters
        this.headers = query.buildAdditionalHeaders();
    }

    private Map<String,String> mergeQueryParameters(Optional<Map<String, List<String>>) {

    }

    public CompletionStage<T> invokeGETRequest() {
        HttpClient.newBuilder().
        var requestBuilder = new SupabaseHttpRequest();
        return client
                .
                .sendAsync(request, BodyHandlers.ofString())
                .thenApply(this::deserializeIntoPOJO); //process into responseType
    }

    public T deserializeIntoPOJO(HttpResponse<String> jsonResponse) {
        //TODO: Handle other status codes other than 200's
        String jsonBody = jsonResponse.body();
        try {
            return this.mapper.readValue(jsonBody, responseType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e); //TODO: Handle this exceptoin
        }
    }
}
