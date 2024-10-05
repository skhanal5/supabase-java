package com.skhanal5.core;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class SupabaseHttpRequestSenderTest {

  private static ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void testConstructorMinimal() {
    var sender = new SupabaseHttpRequestSender(null);
    Assertions.assertNotNull(sender.client);
    Assertions.assertNull(sender.mapper);
  }

  @Test
  void testConstructorSetsCorrectValues() {
    var sender = new SupabaseHttpRequestSender(objectMapper);
    Assertions.assertEquals(objectMapper, sender.mapper);
    Assertions.assertNotNull(sender.client);
  }

  @NoArgsConstructor
  @AllArgsConstructor
  @Getter
  @Setter
  @EqualsAndHashCode
  public static class Data {
    String message;
    int value;
  }

  @Test
  void testInvokeRequestHappyPath()
      throws JsonProcessingException, ExecutionException, InterruptedException {
    var client = mock(HttpClient.class);
    var response = mock(HttpResponse.class);
    var request = mock(SupabaseHttpRequest.class);
    var expectedResponse = new Data("foo", 1);
    when(response.statusCode()).thenReturn(200);
    when(response.body()).thenReturn(" { \"message\": \"foo\", \"value\": 1 }");
    when(client.sendAsync(any(), eq(BodyHandlers.ofString())))
        .thenReturn(CompletableFuture.completedFuture(response));

    var sender = new SupabaseHttpRequestSender(client, objectMapper);
    var res = sender.invokeRequest("", request, Data.class).get();
    Assertions.assertEquals(expectedResponse, res);
  }

  @Test
  void testInvokeRequestUnhappyPath()
      throws JsonProcessingException, ExecutionException, InterruptedException {
    var client = mock(HttpClient.class);
    var response = mock(HttpResponse.class);
    var request = mock(SupabaseHttpRequest.class);
    when(response.statusCode()).thenReturn(400);
    when(client.sendAsync(any(), eq(BodyHandlers.ofString())))
        .thenReturn(CompletableFuture.completedFuture(response));

    var sender = new SupabaseHttpRequestSender(client, objectMapper);
    var result = sender.invokeRequest("", request, Data.class);
    Assertions.assertThrows(ExecutionException.class, () -> result.get());
  }

  @ParameterizedTest
  @ValueSource(ints = {200, 201, 299})
  void testValidateStatusCodeHappy(int statusCode) {
    var response = mock(HttpResponse.class);
    var sender = new SupabaseHttpRequestSender(objectMapper);
    when(response.statusCode()).thenReturn(statusCode);
    Assertions.assertEquals(response, sender.validateStatusCode(response));
  }

  @ParameterizedTest
  @ValueSource(ints = {199, 300, 400, 500})
  void testValidateStatusCodeUnhappy(int statusCode) {
    var response = mock(HttpResponse.class);
    var sender = new SupabaseHttpRequestSender(objectMapper);
    when(response.statusCode()).thenReturn(statusCode);
    var exception =
        Assertions.assertThrows(RuntimeException.class, () -> sender.validateStatusCode(response));
    Assertions.assertEquals(
        exception.getMessage(), "Received an invalid status code from the server: " + statusCode);
  }

  @Test
  void testDeserializeHappyPath() {
    var body = " { \"message\": \"foo\", \"value\": 1 }";
    var sender = new SupabaseHttpRequestSender(objectMapper);
    var result = sender.deserialize(body, Data.class);
    Assertions.assertEquals("foo", result.message);
    Assertions.assertEquals(1, result.value);
  }

  @Test
  void testDeserializeUnhappyPath() {
    var body = " { \"message\": \"foo\", \"value\": 1 }";
    var sender = new SupabaseHttpRequestSender(objectMapper);
    Assertions.assertThrows(RuntimeException.class, () -> sender.deserialize(body, String.class));
  }
}
