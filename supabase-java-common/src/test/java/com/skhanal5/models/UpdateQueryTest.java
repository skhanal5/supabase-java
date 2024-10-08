package com.skhanal5.models;

import com.skhanal5.constants.HeaderType;
import com.skhanal5.models.UpdateQuery.UpdateQueryBuilder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UpdateQueryTest {

  @Test
  void testBuildUpdateQueryMinimal() {
    Assertions.assertThrows(
        NullPointerException.class, () -> new UpdateQuery.UpdateQueryBuilder().build());
  }

  @Test
  void testBuildUpdateQuery() {
    var expectedTable = "foo";
    var expectedSelect = true;
    List<Map<String, Object>> expectedValuesToUpdate = List.of(Map.of("foo", "bar"));
    var expectedFilter = new Filter.FilterBuilder().build();

    var updateQuery =
        new UpdateQueryBuilder()
            .from(expectedTable)
            .update(expectedValuesToUpdate.getFirst())
            .select()
            .filter(expectedFilter)
            .build();

    Assertions.assertEquals(expectedTable, updateQuery.getTable());
    Assertions.assertEquals(expectedSelect, updateQuery.getSelect());
    Assertions.assertEquals(expectedValuesToUpdate, updateQuery.getValuesToUpdate());
    Assertions.assertEquals(expectedFilter, updateQuery.getFilter());
  }

  @Test
  void testBuildQueryParamsMinimal() {
    var updateQuery =
        new UpdateQueryBuilder()
            .from("foo")
            .update(Map.of("baz", "bar"))
            .select()
            .filter(new Filter.FilterBuilder().build())
            .build();

    var actualQueryParams = updateQuery.buildQueryParams();
    Assertions.assertTrue(actualQueryParams.isPresent());
    Assertions.assertEquals(Optional.of(Map.of()), actualQueryParams);
  }

  @Test
  void testBuildQueryParamsWithValues() {
    var updateQuery =
        new UpdateQueryBuilder()
            .from("foo")
            .update(Map.of("baz", "bar"))
            .select()
            .filter(new Filter.FilterBuilder().equals("baz", "bin").build())
            .build();

    var expectedQueryParams = Optional.of(Map.of("baz", "eq.bin"));
    var actualQueryParams = updateQuery.buildQueryParams();

    Assertions.assertTrue(actualQueryParams.isPresent());
    Assertions.assertEquals(expectedQueryParams, actualQueryParams);
  }

  @Test
  void testBuildAdditionalMinimal() {
    var updateQuery =
        new UpdateQueryBuilder()
            .from("foo")
            .update(Map.of("baz", "bar"))
            .filter(new Filter.FilterBuilder().equals("baz", "bin").build())
            .build();

    var actualHeaders = updateQuery.buildAdditionalHeaders();

    Assertions.assertNotNull(actualHeaders);
    Assertions.assertTrue(actualHeaders.isEmpty());
  }

  @Test
  void testBuildAdditionalHeaders() {
    var updateQuery =
        new UpdateQueryBuilder()
            .from("foo")
            .update(Map.of("baz", "bar"))
            .filter(new Filter.FilterBuilder().equals("baz", "bin").build())
            .select()
            .build();

    var actualHeaders = updateQuery.buildAdditionalHeaders();

    Assertions.assertTrue(actualHeaders.isPresent());
    Assertions.assertEquals(HeaderType.RETRIEVE_RESPONSE_VALUES, actualHeaders.get());
  }

  @Test
  void testBuildRequestBody() {
    var updateQuery =
        new UpdateQueryBuilder()
            .from("foo")
            .update(Map.of("baz", "bar"))
            .filter(new Filter.FilterBuilder().equals("baz", "bin").build())
            .select()
            .build();

    var actualRequestBody = updateQuery.buildRequestBody();
    var expectedRequestBody = List.of(Map.of("baz", "bar"));
    Assertions.assertTrue(actualRequestBody.isPresent());
    Assertions.assertEquals(expectedRequestBody, actualRequestBody.get());
  }
}
