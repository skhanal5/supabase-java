package com.skhanal5.core.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skhanal5.models.Query;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;

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

    public CompletableFuture<T> invokeGETRequest() {
        var request = toGetRequest(baseURI, table, queryParameters, headers);
        return client
                .sendAsync(request, BodyHandlers.ofString())
                .thenApply(this::deserializeIntoPOJO); //process into responseType
    }

    public CompletableFuture<T> invokePOSTRequest() {
        var request = toPostRequest(baseURI,table,queryParameters,headers,null);
        return client
                .sendAsync(request, BodyHandlers.ofString())
                .thenApply(this::deserializeIntoPOJO); //process into responseType
    }

    public HttpRequest toGetRequest(String baseURI, String table, Map<String, String> headers, Optional<Map<String, String>> queryParameters) {
        var requestBuilder = HttpRequest.newBuilder();
        var uri = buildURI(baseURI, table, queryParameters);
        headers.forEach((key, value) -> requestBuilder.setHeader(key, value.toString()));
        return requestBuilder
                .uri(uri)
                .GET()
                .build();
    }

    public HttpRequest toPostRequest(String baseURI, String table, Map<String, String> headers, Optional<Map<String, String>> queryParameters, Map<String,String> requestBody) {
        var requestBuilder = HttpRequest.newBuilder();
        var uri = buildURI(baseURI, table, queryParameters);
        headers.forEach((key, value) -> requestBuilder.setHeader(key, value.toString()));
        return requestBuilder
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();
    }

    public static URI buildURI(String baseURI, String path, Optional<Map<String, String>> queryParameters) {
        if (queryParameters.isPresent()) {
            String fullURI = baseURI + "/" + path + "?" + serializeQueryParameters(queryParameters.get());
            return URI.create(fullURI);
        }
        String fullURI = baseURI + "/" + path;
        return URI.create(fullURI);
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

    private static StringJoiner serializeQueryParameters(Map<String, String> queryParameters) {
        StringJoiner stringifyPathParams = new StringJoiner("&");
        for (Entry<String,String> keyValuePair : queryParameters.entrySet()) {
            String currPathParam = keyValuePair.getKey() + "=" + keyValuePair.getValue();
            stringifyPathParams
                    .add(currPathParam);
        }
        return stringifyPathParams;
    }
}
