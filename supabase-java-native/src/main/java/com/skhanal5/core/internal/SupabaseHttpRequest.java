package com.skhanal5.core.internal;

import com.skhanal5.models.Query;
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

  Map<String, String> headers;

  Optional<Map<String, String>> queryParameters;

  Optional<List<Map<String, Object>>> requestBody;

  public SupabaseHttpRequest(
      String baseURI, String table, Map<String, String> headers, Query query) {
    this.baseURI = baseURI;
    this.table = table;
    this.headers = headers;
    this.queryParameters = query.buildQueryParams();
    this.requestBody = query.buildRequestBody();
  }

  public HttpRequest toHttpGetRequest() {
    var requestBuilder = HttpRequest.newBuilder();
    var uri = buildURI(baseURI, table, queryParameters);
    headers.forEach((key, value) -> requestBuilder.setHeader(key, value.toString()));
    return requestBuilder.uri(uri).build();
  }

  public HttpRequest toHttpPostRequest() {
    var requestBuilder = HttpRequest.newBuilder();
    var uri = buildURI(baseURI, table, queryParameters);
    headers.forEach((key, value) -> requestBuilder.setHeader(key, value.toString()));
    return requestBuilder
        .uri(uri)
        .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
        .build();
  }

  private static HttpRequest.Builder addHeaders(
      HttpRequest.Builder requestBuilder, Map<String, List<String>> headers) {
    headers.forEach((key, value) -> requestBuilder.setHeader(key, value.toString()));
    return requestBuilder;
  }

  public static URI buildURI(
      String baseURI, String path, Optional<Map<String, String>> queryParameters) {
    if (queryParameters.isPresent()) {
      String fullURI = baseURI + "/" + path + "?" + serializeQueryParameters(queryParameters.get());
      return URI.create(fullURI);
    }
    String fullURI = baseURI + "/" + path;
    return URI.create(fullURI);
  }

  private static StringJoiner serializeQueryParameters(Map<String, String> queryParameters) {
    StringJoiner stringifyPathParams = new StringJoiner("&");
    for (Entry<String, String> keyValuePair : queryParameters.entrySet()) {
      String currPathParam = keyValuePair.getKey() + "=" + keyValuePair.getValue();
      stringifyPathParams.add(currPathParam);
    }
    return stringifyPathParams;
  }
}
