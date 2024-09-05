package com.skhanal5.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.CompletableFuture;

class SupabaseHttpRequestSender {

  HttpClient client;

  ObjectMapper mapper;

  SupabaseHttpRequestSender(HttpClient client, ObjectMapper mapper) {
    this.client = client;
    this.mapper = mapper;
  }

  <T> CompletableFuture<T> invokeRequest(
      String requestMethod, SupabaseHttpRequest request, Class<T> responseType)
      throws JsonProcessingException {
    var httpRequest = request.buildRequest(requestMethod);
    return client
        .sendAsync(httpRequest, BodyHandlers.ofString())
        .thenApply(e -> deserializeIntoPOJO(e, responseType));
  }

  private <T> T deserializeIntoPOJO(HttpResponse<String> jsonResponse, Class<T> responseType) {
    if (jsonResponse.statusCode() >= 200 || jsonResponse.statusCode() < 300) {
      var jsonBody = jsonResponse.body();

      if (jsonBody != null && jsonBody.length() > 1) {
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
}
