package com.skhanal5.core.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skhanal5.models.Query;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.*;
import java.util.Map.Entry;

public class SupabaseHttpRequest {

    URI uri;

    Map<String, String> queryParameters;

    List<Map<String, Object>> requestBody;

    Map<String, String> headers;

    private static final ObjectMapper requestMapper = new ObjectMapper();

    public SupabaseHttpRequest(String baseURI,
                               Map<String,String> defaultHeaders,
                               Query query) {
        this.uri = buildURI(baseURI, query.getTable(), queryParameters);
        this.queryParameters = query.buildQueryParams().orElse(Map.of());
        this.requestBody = query.buildRequestBody().orElse(List.of());
        this.headers = mergeHeaders(defaultHeaders, query.buildAdditionalHeaders());
    }

    private Map<String,String> mergeHeaders(Map<String,String> headers, Optional<Map<String,String>> headersToAdd) {
        var mergedHeaders = new HashMap<>(headers);
        headersToAdd.ifPresent(mergedHeaders::putAll);
        return mergedHeaders;
    }

   public  HttpRequest buildRequest(String methodName) throws JsonProcessingException {
        var requestBuilder = HttpRequest.newBuilder();
        headers.forEach(requestBuilder::setHeader);
        headers.put("Content-Type", "application/json"); //move this inside of the query's buildHeaders method as needed
        String requestBodyToJsonString = requestMapper.writeValueAsString(requestBody);
        return requestBuilder
                .uri(uri)
                .method(methodName,  HttpRequest.BodyPublishers.ofString(requestBodyToJsonString))
                .build();
    }

    private static URI buildURI(String baseURI, String path, Map<String, String> queryParameters) {
        String fullURI = baseURI + path + "?" + serializeQueryParameters(queryParameters);
        return URI.create(fullURI);
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
