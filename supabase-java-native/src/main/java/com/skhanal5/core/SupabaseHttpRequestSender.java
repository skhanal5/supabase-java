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

  SupabaseHttpRequestSender(ObjectMapper mapper) {
    this.client = HttpClient.newHttpClient();
    this.mapper = mapper;
  }

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
        .thenApply(this::validateStatusCode)
        .thenApply(e -> deserialize(e.body(), responseType));
  }

  HttpResponse<String> validateStatusCode(HttpResponse<String> response) {
    var statusCode = response.statusCode();
    var isStatusCodeValue = statusCode >= 200 && statusCode < 300;
    if (isStatusCodeValue) {
      return response;
    }
    throw new RuntimeException("Received an invalid status code from the server: " + response.statusCode()); // replace with an actual exception
  }

//  <T> T deserializeIntoPOJO(HttpResponse<String> jsonResponse, Class<T> responseType) {
//    if (jsonResponse.statusCode() >= 200 || jsonResponse.statusCode() < 300) {
//      var jsonBody = jsonResponse.body();
//
//      if (jsonBody != null && jsonBody.length() > 1) {
//        jsonBody = jsonBody.substring(1, jsonBody.length() - 1);
//      }
//
//      if (responseType == String.class) {
//        return responseType.cast(jsonBody);
//      }
//
//      try {
//        return this.mapper.readValue(jsonBody, responseType);
//      } catch (JsonProcessingException e) {
//        throw new RuntimeException(e);
//      }
//    }
//    // TODO: Handle other status codes other than 200's
//    return null;
//  }

  <T> T deserialize(String responseBody, Class<T> responseType) {
    try {
      return this.mapper.readValue(responseBody, responseType);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
