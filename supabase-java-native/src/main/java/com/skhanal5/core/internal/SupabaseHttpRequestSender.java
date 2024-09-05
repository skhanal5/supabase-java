package com.skhanal5.core.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skhanal5.models.Query;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

public class SupabaseHttpRequestSender<T> {

  HttpClient client;

  String table;

  Optional<Map<String, String>> queryParameters;

  Optional<List<Map<String, Object>>> requestBody;

  Map<String, String> headers;

  ObjectMapper mapper;

  Class<T> responseType;

  String baseURI;

  public SupabaseHttpRequestSender(
      String baseURI,
      HttpClient client,
      Map<String, String> defaultHeaders,
      Query query,
      Class<T> responseType,
      ObjectMapper mapper) {
    this.baseURI = baseURI;
    this.client = client;
    this.table = query.getTable();
    this.queryParameters = query.buildQueryParams();
    this.requestBody = query.buildRequestBody();
    this.headers = mergeHeaders(defaultHeaders, query.buildAdditionalHeaders());
    this.responseType = responseType;
    this.mapper = mapper;
  }

  public CompletableFuture<T> invokeGETRequest() {
    var request = toGetRequest(baseURI, table, headers, queryParameters.orElse(Map.of()));
    return client
        .sendAsync(request, BodyHandlers.ofString())
        .thenApply(this::deserializeIntoPOJO); // process into responseType
  }

  public CompletableFuture<T> invokePOSTRequest() {
    headers.put("Content-Type", "application/json");
    var request =
        toPostRequest(
            baseURI,
            table,
            headers,
            queryParameters.orElse(Map.of()),
            requestBody.orElse(List.of()));
    return client
        .sendAsync(request, BodyHandlers.ofString())
        .thenApply(this::deserializeIntoPOJO); // process into responseType
  }

  public CompletableFuture<T> invokePATCHRequest() {
    headers.put("Content-Type", "application/json");
    var request =
        toPatchRequest(
            baseURI,
            table,
            headers,
            queryParameters.orElse(Map.of()),
            requestBody.orElse(List.of()));
    return client
        .sendAsync(request, BodyHandlers.ofString())
        .thenApply(this::deserializeIntoPOJO); // process into responseType
  }

  public CompletableFuture<T> invokeDELETERequest() {
    var request = toDeleteRequest(baseURI, table, headers, queryParameters.orElse(Map.of()));
    return client
        .sendAsync(request, BodyHandlers.ofString())
        .thenApply(this::deserializeIntoPOJO); // process into responseType
  }

  private HttpRequest toGetRequest(
      String baseURI,
      String table,
      Map<String, String> headers,
      Map<String, String> queryParameters) {
    var requestBuilder = HttpRequest.newBuilder();
    var uri = buildURI(baseURI, table, queryParameters);
    headers.forEach(requestBuilder::setHeader);
    return requestBuilder.uri(uri).GET().build();
  }

  private HttpRequest toPatchRequest(
      String baseURI,
      String table,
      Map<String, String> headers,
      Map<String, String> queryParameters,
      List<Map<String, Object>> requestBody) {
    var requestBuilder = HttpRequest.newBuilder();
    var uri = buildURI(baseURI, table, queryParameters);
    headers.forEach(requestBuilder::setHeader);
    String requestBodyToJsonString = null;
    try {
      requestBodyToJsonString = this.mapper.writeValueAsString(requestBody);
      return requestBuilder
          .uri(uri)
          .method("PATCH", HttpRequest.BodyPublishers.ofString(requestBodyToJsonString))
          .build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  private HttpRequest toPostRequest(
      String baseURI,
      String table,
      Map<String, String> headers,
      Map<String, String> queryParameters,
      List<Map<String, Object>> requestBody) {
    var requestBuilder = HttpRequest.newBuilder();
    var uri = buildURI(baseURI, table, queryParameters);
    headers.forEach(requestBuilder::setHeader);
    String requestBodyToJsonString = null;
    try {
      requestBodyToJsonString = this.mapper.writeValueAsString(requestBody);
      return requestBuilder
          .uri(uri)
          .POST(HttpRequest.BodyPublishers.ofString(requestBodyToJsonString))
          .build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  private HttpRequest toDeleteRequest(
      String baseURI,
      String table,
      Map<String, String> headers,
      Map<String, String> queryParameters) {
    var requestBuilder = HttpRequest.newBuilder();
    var uri = buildURI(baseURI, table, queryParameters);
    headers.forEach(requestBuilder::setHeader);
    return requestBuilder.uri(uri).DELETE().build();
  }

  private static URI buildURI(String baseURI, String path, Map<String, String> queryParameters) {
    String fullURI = baseURI + path + "?" + serializeQueryParameters(queryParameters);
    return URI.create(fullURI);
  }

  private T deserializeIntoPOJO(HttpResponse<String> jsonResponse) {
    if (jsonResponse.statusCode() >= 200 || jsonResponse.statusCode() < 300) {
      var jsonBody = jsonResponse.body();

      if (jsonBody != null || jsonBody.length() > 1) {
        jsonBody = jsonBody.substring(1, jsonBody.length() - 1);
      }

      if (responseType == String.class) {
        return responseType.cast(jsonBody);
      }

      try {
        return this.mapper.readValue(jsonBody, responseType);
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    }
    // TODO: Handle other status codes other than 200's
    return null;
  }

  private Map<String, String> mergeHeaders(
      Map<String, String> defaultHeaders, Optional<Map<String, String>> additionalHeaders) {
    var mergedHeaders = new HashMap<>(defaultHeaders);
    additionalHeaders.ifPresent(mergedHeaders::putAll);
    return mergedHeaders;
  }

  private static StringJoiner serializeQueryParameters(Map<String, String> queryParameters) {
    StringJoiner stringifyPathParams = new StringJoiner("&");
    for (Entry<String, String> keyValuePair : queryParameters.entrySet()) {
      String currPathParam = keyValuePair.getKey() + "=" + encodeSpaces(keyValuePair.getValue());
      stringifyPathParams.add(currPathParam);
    }
    return stringifyPathParams;
  }

  private static String encodeSpaces(String URI) {
    return URI.replace(" ", "%20");
  }
}
