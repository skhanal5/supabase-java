package com.skhanal5.core.internal;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.StringJoiner;

public class SupabaseHttpRequest {

    String baseURI;

    String table;

    Map<String,String > headers;

    Optional<Map<String, String>> queryParameters;

    public SupabaseHttpRequest(String baseURI, String table, Map<String, String> headers, Optional<Map<String, String>> queryParameters) {
        this.baseURI = baseURI;
        this.table = table;
        this.headers = headers;
        this.queryParameters = queryParameters;
    }

    public HttpRequest toHttpRequest() {
        var requestBuilder = HttpRequest.newBuilder();
        var uri = buildURI(baseURI, table, queryParameters);
        headers.forEach((key, value) -> requestBuilder.setHeader(key, value.toString()));
        return requestBuilder
                .uri(uri)
                .build();
    }

    private static HttpRequest.Builder addHeaders(HttpRequest.Builder requestBuilder, Map<String, List<String>> headers) {
        headers.forEach((key, value) -> requestBuilder.setHeader(key, value.toString()));
        return requestBuilder;
    }

    public static URI buildURI(String baseURI, String path, Optional<Map<String, String>> queryParameters) {
        if (queryParameters.isPresent()) {
            String fullURI = baseURI + "/" + path + "?" + serializeQueryParameters(queryParameters.get());
            return URI.create(fullURI);
        }
        String fullURI = baseURI + "/" + path;
        return URI.create(fullURI);
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
