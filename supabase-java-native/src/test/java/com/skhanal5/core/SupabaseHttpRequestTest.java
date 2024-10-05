package com.skhanal5.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.skhanal5.models.Query;
import com.skhanal5.models.SelectQuery;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

class SupabaseHttpRequestTest {

  @Test
  void testSupabaseConstructorMinimal() {
    var query = Mockito.mock(Query.class);
    var request = new SupabaseHttpRequest(null, Map.of(), query);

    Assertions.assertEquals(Map.of(), request.headers);
    Assertions.assertEquals(List.of(), request.requestBody);
    Assertions.assertEquals(Map.of(), request.queryParameters);
    Assertions.assertEquals("nullnull?", request.uri.toString());
  }

  @Test
  void testMergeHeadersNull() {
    Assertions.assertThrows(
        NullPointerException.class, () -> SupabaseHttpRequest.mergeHeaders(null, Optional.empty()));
  }

  @Test
  void testMergeHeadersMinimal() {
    var res = SupabaseHttpRequest.mergeHeaders(Map.of(), Optional.empty());
    Assertions.assertEquals(Map.of(), res);
  }

  @Test
  void testMergeHeadersWithValues() {
    var res =
        SupabaseHttpRequest.mergeHeaders(Map.of("foo", "bar"), Optional.of(Map.of("baz", "bag")));
    Assertions.assertEquals(Map.of("foo", "bar", "baz", "bag"), res);
  }

  @Test
  void testBuildRequestSetsCorrectMethodName() throws JsonProcessingException {
    var query = Mockito.mock(Query.class);
    var expectedMethod = "GET";
    var supabaseRequest = new SupabaseHttpRequest("http://localhost", Map.of(), query);
    var httpRequest = supabaseRequest.buildRequest(expectedMethod);
    Assertions.assertEquals(expectedMethod, httpRequest.method());
  }

  @Test
  void testBuildRequestSetsCorrectURI() throws JsonProcessingException {
    var query = new SelectQuery.SelectQueryBuilder().from("foo").select("2").build();
    var expectedURI = "http://localhost/foo?select=2";
    var supabaseRequest = new SupabaseHttpRequest("http://localhost/", Map.of(), query);
    var httpRequest = supabaseRequest.buildRequest("GET");
    Assertions.assertEquals(expectedURI, httpRequest.uri().toString());
  }

  @Test
  void testBuildRequestSetsDefaultHeaders() throws JsonProcessingException {
    var expectedHeaders = Map.of("Content-Type", List.of("application/json"));
    var query = Mockito.mock(Query.class);
    var supabaseRequest = new SupabaseHttpRequest("http://localhost", Map.of(), query);
    var httpRequest = supabaseRequest.buildRequest("GET");
    Assertions.assertEquals(expectedHeaders, httpRequest.headers().map());
  }

  @Test
  void testBuildURIMinimal() {
    var res = SupabaseHttpRequest.buildURI("", "", Map.of());
    Assertions.assertEquals("?", res.toString());
  }

  @Test
  void testBuildURIWithBaseURIAndPath() {
    var res = SupabaseHttpRequest.buildURI("http://localhost", "/api", Map.of());
    Assertions.assertEquals("http://localhost/api?", res.toString());
  }

  @Test
  void testBuildURIWithAllValues() {
    var res = SupabaseHttpRequest.buildURI("http://localhost", "/api", Map.of("foo", "bar"));
    Assertions.assertEquals("http://localhost/api?foo=bar", res.toString());
  }

  private static Stream<Arguments> provideMinimalInputs() {
    return Stream.of(Arguments.of(Map.of(), null));
  }

  @ParameterizedTest
  @MethodSource("provideMinimalInputs")
  public void testSerializeQueryParametersMinimal(Map<String, String> queryParameters) {
    var res = SupabaseHttpRequest.serializeQueryParameters(queryParameters);
    Assertions.assertEquals("", res);
  }

  @Test
  public void testSerializeQueryParametersWithOneMapping() {
    var res = SupabaseHttpRequest.serializeQueryParameters(Map.of("foo", "bar"));
    Assertions.assertEquals("foo=bar", res);
  }

  @Test
  public void testSerializeQueryParametersWithMultipleMappings() {
    Map<String, String> orderedMap = new TreeMap<>(Map.of("foo", "bar", "bar", "baz"));
    var res = SupabaseHttpRequest.serializeQueryParameters(orderedMap);
    Assertions.assertEquals("bar=baz&foo=bar", res);
  }

  @Test
  public void testEncodeSpacesNull() {
    Assertions.assertThrows(
        NullPointerException.class, () -> SupabaseHttpRequest.encodeSpaces(null));
  }

  @ParameterizedTest
  @ValueSource(strings = {"nospaces", "one space", "multiple  spaces"})
  public void testEncodeSpacesRemovesAllSpaces(String testInput) {
    var result = SupabaseHttpRequest.encodeSpaces(testInput);
    Assertions.assertFalse(result.contains(" "));
  }

  @Test
  public void testEncodeSpacesReplacesSpaceCorrectly() {
    var stringToEncode = "multiple  space";
    var expectedEncodedResult = "multiple%20%20space";
    var actualResult = SupabaseHttpRequest.encodeSpaces(stringToEncode);
    Assertions.assertEquals(expectedEncodedResult, actualResult);
  }
}
