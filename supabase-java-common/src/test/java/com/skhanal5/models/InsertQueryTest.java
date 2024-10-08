package com.skhanal5.models;

import com.skhanal5.constants.HeaderType;
import com.skhanal5.models.InsertQuery.InsertQueryBuilder;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InsertQueryTest {

  @Test
  void testBuildInsertQueryMinimal() {
    Assertions.assertThrows(
        NullPointerException.class, () -> new InsertQuery.InsertQueryBuilder().build());
  }

  @Test
  void testBuildInsertQuery() {
    var expectedTable = "foo";
    var expectedSelect = true;
    List<Map<String, Object>> expectedInsert = List.of(Map.of("foo", "bar"));

    var insertQuery =
        new InsertQueryBuilder()
            .from(expectedTable)
            .insert(expectedInsert.getFirst())
            .select()
            .build();

    Assertions.assertEquals(expectedTable, insertQuery.getTable());
    Assertions.assertEquals(expectedSelect, insertQuery.getSelect());
    Assertions.assertEquals(expectedInsert, insertQuery.getValuesToInsert());
  }

  @Test
  void testBuildAdditionalHeadersMinimal() {
    var insertQuery =
        new InsertQuery.InsertQueryBuilder().from("foo").insert(Map.of("bar", "baz")).build();

    var actualHeaders = insertQuery.buildAdditionalHeaders();

    Assertions.assertNotNull(actualHeaders);
    Assertions.assertTrue(actualHeaders.isEmpty());
  }

  @Test
  void testBuildAdditionalHeadersWithValues() {
    var insertQuery =
        new InsertQueryBuilder().from("foo").insert(Map.of("bar", "baz")).select().build();

    var actualHeaders = insertQuery.buildAdditionalHeaders();
    Assertions.assertTrue(actualHeaders.isPresent());
    Assertions.assertEquals(HeaderType.RETRIEVE_RESPONSE_VALUES, actualHeaders.get());
  }

  @Test
  void testBuildRequestBody() {
    var insertQuery =
        new InsertQuery.InsertQueryBuilder().from("foo").insert(Map.of("bar", "baz")).build();

    var actualRequestBody = insertQuery.buildRequestBody();
    var expectedRequestBody = List.of(Map.of("bar", "baz"));
    Assertions.assertTrue(actualRequestBody.isPresent());
    Assertions.assertEquals(expectedRequestBody, actualRequestBody.get());
  }

  @Test
  void testBuildQueryParams() {
    var insertQuery =
        new InsertQuery.InsertQueryBuilder().from("foo").insert(Map.of("bar", "baz")).build();

    var actualQueryParams = insertQuery.buildQueryParams();
    Assertions.assertTrue(actualQueryParams.isEmpty());
  }
}
