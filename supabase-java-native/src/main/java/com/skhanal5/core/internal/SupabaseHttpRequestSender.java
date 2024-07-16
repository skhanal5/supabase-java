package com.skhanal5.core.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skhanal5.models.Query;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;

public class SupabaseHttpRequestSender<T> {

    HttpClient client;

    String table;

    Optional<Map<String,String>> queryParameters;

    Map<String, String> headers;

    ObjectMapper mapper;

    Class<T> responseType;

    String baseURI;


    public SupabaseHttpRequestSender(String baseURI,
                                     HttpClient client,
                                     Map<String,String> defaultHeaders,
                                     Query query,
                                     Class<T> responseType,
                                     ObjectMapper mapper) {
        this.baseURI = baseURI;
        this.client = client;
        this.table = query.getTable();
        this.queryParameters = query.buildQueryParams();
        this.headers = defaultHeaders;
        this.responseType = responseType;
        this.mapper = mapper;
    }


    public CompletableFuture<T> invokeGETRequest() {
        var request = toGetRequest(baseURI, table, headers, queryParameters);
        return client
                .sendAsync(request, BodyHandlers.ofString())
                .thenApply(this::deserializeIntoPOJO); //process into responseType
    }

    public CompletableFuture<T> invokePOSTRequest() {
        var request = toPostRequest(baseURI,table, headers, queryParameters, null);
        return client
                .sendAsync(request, BodyHandlers.ofString())
                .thenApply(this::deserializeIntoPOJO); //process into responseType
    }

    public HttpRequest toGetRequest(String baseURI, String table, Map<String, String> headers, Optional<Map<String, String>> queryParameters) {
        var requestBuilder = HttpRequest.newBuilder();
        var uri = buildURI(baseURI, table, queryParameters);
        System.out.println(uri);
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
            String fullURI = baseURI + path + "?" + serializeQueryParameters(queryParameters.get());
            return URI.create(fullURI);
        }
        String fullURI = baseURI + path;
        return URI.create(fullURI);
    }

    public T deserializeIntoPOJO(HttpResponse<String> jsonResponse) {
        if (jsonResponse.statusCode() >= 200 || jsonResponse.statusCode() <= 200) {
            var jsonBody = jsonResponse.body();
            jsonBody = jsonBody.substring(1,jsonBody.length()-1);

            if (responseType == String.class) {
                return responseType.cast(jsonBody);
            }

            try {
                return this.mapper.readValue(jsonBody, responseType);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        //TODO: Handle other status codes other than 200's
        return null;
    }

    private static StringJoiner serializeQueryParameters(Map<String, String> queryParameters) {
        StringJoiner stringifyPathParams = new StringJoiner("&");
        for (Entry<String, String> keyValuePair : queryParameters.entrySet()) {
            String currPathParam = keyValuePair.getKey() + "=" + encodeSpaces(keyValuePair.getValue());
            stringifyPathParams
                    .add(currPathParam);
        }
        return stringifyPathParams;
    }

    private static String encodeSpaces(String URI) {
        return URI.replace(" ", "%20");
    }
}
