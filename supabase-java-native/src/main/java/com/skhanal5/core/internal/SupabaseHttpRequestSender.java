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

    Map<String,String> queryParameters;

    Optional<Map<String, String>> headers;

    ObjectMapper mapper;

    Class<T> responseType;

    String baseURI;


    public SupabaseHttpRequestSender(String baseURI,
                                     HttpClient client,
                                     Map<String,String> defaultHeaders,
                                     Query query,
                                     Class<T> responseType) {
        this.baseURI = baseURI;
        this.client = client;
        this.table = query.getTable();
        this.queryParameters = mergeQueryParameters(defaultHeaders, query.buildQueryParams()); //combine queryParameters
        this.headers = query.buildAdditionalHeaders();
        this.responseType = responseType;
    }

    private Map<String,String> mergeQueryParameters(Map<String,String> defaultHeaders, Optional<Map<String, String>> otherQueryParams) {
        if (otherQueryParams.isPresent()) {
            var otherQueryParamsUnwrapped = otherQueryParams.get();
            defaultHeaders.putAll(otherQueryParamsUnwrapped);
        }
        return defaultHeaders;
    }

    public CompletionStage<T> invokeGETRequest() {
        var request = new SupabaseHttpRequest(baseURI, table, queryParameters, headers);
        return client
                .sendAsync(request.toHttpRequest(), BodyHandlers.ofString())
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
