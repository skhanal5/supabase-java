package com.skhanal5.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skhanal5.models.Query;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.*;
import java.util.Map.Entry;

class SupabaseHttpRequest {

  URI uri;

  Map<String, String> queryParameters;

  List<Map<String, Object>> requestBody;

  Map<String, String> headers;

  private static final ObjectMapper requestMapper = new ObjectMapper();

  SupabaseHttpRequest(String baseURI, Map<String, String> defaultHeaders, Query query) {
    this.queryParameters = query.buildQueryParams().orElse(Map.of());
    this.uri = buildURI(baseURI, query.getTable(), queryParameters);
    this.requestBody = query.buildRequestBody().orElse(List.of());
    this.headers = mergeHeaders(defaultHeaders, query.buildAdditionalHeaders());
  }

  HttpRequest buildRequest(String methodName) throws JsonProcessingException {
    var requestBuilder = HttpRequest.newBuilder();
    headers.put(
            "Content-Type",
            "application/json"); // move this inside of the query's buildHeaders method as needed
    headers.forEach(requestBuilder::setHeader);
    String requestBodyToJsonString = requestMapper.writeValueAsString(requestBody);
    return requestBuilder
        .uri(uri)
        .method(methodName, HttpRequest.BodyPublishers.ofString(requestBodyToJsonString))
        .build();
  }

  static Map<String, String> mergeHeaders(
          Map<String, String> headers, Optional<Map<String, String>> headersToAdd) {
    var mergedHeaders = new HashMap<>(headers);
    headersToAdd.ifPresent(mergedHeaders::putAll);
    return mergedHeaders;
  }

  static URI buildURI(String baseURI, String path, Map<String, String> queryParameters) {
    String fullURI = baseURI + path + "?" + serializeQueryParameters(queryParameters);
    return URI.create(fullURI);
  }

  static String serializeQueryParameters(Map<String, String> queryParameters) {
    if (queryParameters == null || queryParameters.isEmpty()) {
      return "";
    }

    StringJoiner stringifyPathParams = new StringJoiner("&");
    for (Entry<String, String> keyValuePair : queryParameters.entrySet()) {
      String currPathParam = keyValuePair.getKey() + "=" + encodeSpaces(keyValuePair.getValue());
      stringifyPathParams.add(currPathParam);
    }
    return stringifyPathParams.toString();
  }

  static String encodeSpaces(String URI) {
    return URI.replace(" ", "%20");
  }
}
