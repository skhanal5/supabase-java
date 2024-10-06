package com.skhanal5.core;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class SupabaseClientTest {

  @Test
  void testNewInstanceWithNullDatabaseURL() {
    Assertions.assertThrows(NullPointerException.class, () -> SupabaseClient.newInstance(null, ""));
  }

  @Test
  void testNewInstanceWithNullServiceKey() {
    Assertions.assertThrows(NullPointerException.class, () -> SupabaseClient.newInstance("", null));
  }

  @Test
  void testNewInstanceSetsWebClient() {
    var url = "";
    var key = "";
    var supabaseClient = SupabaseClient.newInstance(url, key);
    Assertions.assertNotNull(supabaseClient.client);
  }

  private static Stream<Arguments> provideInputAndExpectedResult() {
    var emptyInput = Optional.empty();
    var emptyMapInput = Optional.of(Map.of());
    var populatedMapInput = Optional.of(Map.of("foo", "bar"));

    var emptyOutput = new LinkedMultiValueMap<String, String>();
    var populatedMapOutput = new LinkedMultiValueMap<String, String>();
    populatedMapOutput.put("foo", List.of("bar"));

    return Stream.of(
        arguments(emptyInput, emptyOutput),
        arguments(emptyMapInput, emptyOutput),
        arguments(populatedMapInput, populatedMapOutput));
  }

  @ParameterizedTest
  @MethodSource("provideInputAndExpectedResult")
  void testToMultiLevel(
      Optional<Map<String, String>> input, MultiValueMap<String, String> expectedResult) {
    var client = SupabaseClient.newInstance("", "");
    var result = client.toMultiValueMap(input);
    Assertions.assertEquals(expectedResult, result);
  }

  @ParameterizedTest
  @MethodSource("provideInputAndExpectedResult")
  void testConstructHttpHeaders(
      Optional<Map<String, String>> input, MultiValueMap<String, String> expectedResult) {
    var client = SupabaseClient.newInstance("", "");
    var consumer = client.constructHttpHeaders(input);
    var emptyHeaders = new HttpHeaders(new LinkedMultiValueMap<>());
    consumer.accept(emptyHeaders);
    Assertions.assertEquals(expectedResult.toSingleValueMap(), emptyHeaders.toSingleValueMap());
  }
}
