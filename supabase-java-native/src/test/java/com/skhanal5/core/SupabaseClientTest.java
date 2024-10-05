package com.skhanal5.core;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.skhanal5.models.*;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SupabaseClientTest {

  @Test
  void testNewInstanceGivenNullInputs() {
    Assertions.assertThrows(
        NullPointerException.class, () -> SupabaseClient.newInstance(null, null));
  }

  @Test
  void testNewInstanceAlternateGivenNullInputs() {
    Assertions.assertThrows(
        NullPointerException.class, () -> SupabaseClient.newInstance(null, null, null));
  }

  @Test
  void testNewInstanceWithURLAndServiceKey() {
    var client = SupabaseClient.newInstance("", "");
    var expectedHeaders = Map.of("apikey", "", "Authorization", "Bearer ");
    Assertions.assertEquals("/rest/v1/", client.baseURI);
    Assertions.assertNotNull(client.sender);
    Assertions.assertEquals(expectedHeaders, client.defaultHeaders);
  }

  @Test
  void testNewInstanceWithURLServiceKeyAndMapper() {
    var client = SupabaseClient.newInstance("", "");
    var expectedHeaders = Map.of("apikey", "", "Authorization", "Bearer ");
    Assertions.assertEquals("/rest/v1/", client.baseURI);
    Assertions.assertNotNull(client.sender);
    Assertions.assertEquals(expectedHeaders, client.defaultHeaders);
  }

  private static Stream<Arguments> provideDifferentQueryTypes() {
    return Stream.of(
        arguments(mock(SelectQuery.class)),
        arguments(mock(InsertQuery.class)),
        arguments(mock(UpdateQuery.class)),
        arguments(mock(DeleteQuery.class)));
  }

  @MethodSource("provideDifferentQueryTypes")
  @ParameterizedTest
  void testExecuteHappyPath(Query query) throws JsonProcessingException {
    var sender = mock(SupabaseHttpRequestSender.class);
    var client = new SupabaseClient(sender, "", Map.of());

    var expectedResult = CompletableFuture.completedFuture("");
    when(sender.invokeRequest(any(), any(), eq(String.class))).thenReturn(expectedResult);

    var result = client.execute(query, String.class, "");
    Assertions.assertEquals("", result);
  }

  @MethodSource("provideDifferentQueryTypes")
  @ParameterizedTest
  void testExecuteThrowsJsonException(Query query) throws JsonProcessingException {
    var sender = mock(SupabaseHttpRequestSender.class);
    var client = new SupabaseClient(sender, "", Map.of());
    var expectedException = JsonProcessingException.class;

    when(sender.invokeRequest(any(), any(), eq(String.class))).thenThrow(expectedException);

    var exception =
        Assertions.assertThrows(
            RuntimeException.class, () -> client.execute(query, String.class, ""));
    Assertions.assertEquals(expectedException, exception.getCause().getClass());
  }

  private static Stream<Arguments> provideExceptions() {
    return Stream.of(
        arguments(mock(SelectQuery.class), InterruptedException.class),
        arguments(mock(SelectQuery.class), ExecutionException.class),
        arguments(mock(InsertQuery.class), InterruptedException.class),
        arguments(mock(InsertQuery.class), ExecutionException.class),
        arguments(mock(UpdateQuery.class), InterruptedException.class),
        arguments(mock(UpdateQuery.class), ExecutionException.class),
        arguments(mock(DeleteQuery.class), InterruptedException.class),
        arguments(mock(DeleteQuery.class), ExecutionException.class));
  }

  @MethodSource("provideExceptions")
  @ParameterizedTest
  void testExecuteThrowsOtherExceptions(Query query, Class<Throwable> throwableClass)
      throws JsonProcessingException, ExecutionException, InterruptedException {
    var sender = mock(SupabaseHttpRequestSender.class);
    var client = new SupabaseClient(sender, "", Map.of());
    CompletableFuture<String> future = mock(CompletableFuture.class);

    when(sender.invokeRequest(any(), any(), eq(String.class))).thenReturn(future);

    when(future.get()).thenThrow(throwableClass);

    var exception =
        Assertions.assertThrows(
            RuntimeException.class, () -> client.execute(query, String.class, ""));
    Assertions.assertEquals(throwableClass, exception.getCause().getClass());
  }

  @Test
  void testExecuteSelectInvokesExecute() throws JsonProcessingException {
    var query = mock(SelectQuery.class);
    var client = setupMockedClient();

    client.executeSelect(query, String.class);
    verify(client, times(1)).execute(query, String.class, "GET");
  }

  @Test
  void testExecuteInsertInvokesExecute() throws JsonProcessingException {
    var query = mock(InsertQuery.class);
    var client = setupMockedClient();

    client.executeInsert(query, String.class);
    verify(client, times(1)).execute(query, String.class, "POST");
  }

  @Test
  void testExecuteUpdateInvokesExecute() throws JsonProcessingException {
    var query = mock(UpdateQuery.class);
    var client = setupMockedClient();

    client.executeUpdate(query, String.class);
    verify(client, times(1)).execute(query, String.class, "PATCH");
  }

  @Test
  void testExecuteDeleteInvokesExecute() throws JsonProcessingException {
    var query = mock(DeleteQuery.class);
    var client = setupMockedClient();

    client.executeDelete(query, String.class);
    verify(client, times(1)).execute(query, String.class, "DELETE");
  }

  private SupabaseClient setupMockedClient() throws JsonProcessingException {
    var sender = mock(SupabaseHttpRequestSender.class);
    when(sender.invokeRequest(any(), any(), eq(String.class)))
        .thenReturn(CompletableFuture.completedFuture(""));

    return spy(new SupabaseClient(sender, "", Map.of()));
  }
}
