package com.skhanal5.models;

import com.skhanal5.constants.HeaderType;
import com.skhanal5.models.DeleteQuery.DeleteQueryBuilder;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DeleteQueryTest {

  @Test
  void testBuildDeleteQueryMinimal() {
    Assertions.assertThrows(
        NullPointerException.class, () -> new DeleteQuery.DeleteQueryBuilder().build());
  }

  @Test
  void testBuildDeleteQuery() {
    var expectedTable = "foo";
    var expectedSelect = true;
    var expectedFilter = new Filter.FilterBuilder().build();
    var expectedDelete = true;

    var deleteQuery =
        new DeleteQueryBuilder()
            .from(expectedTable)
            .delete()
            .filter(expectedFilter)
            .select()
            .build();

    Assertions.assertEquals(expectedTable, deleteQuery.getTable());
    Assertions.assertEquals(expectedDelete, deleteQuery.getDelete());
    Assertions.assertEquals(expectedSelect, deleteQuery.getSelect());
    Assertions.assertEquals(expectedFilter, deleteQuery.getFilter());
  }

  @Test
  void testBuildQueryParamsWithValues() {
    var deleteQuery =
        new DeleteQueryBuilder()
            .from("foo")
            .delete()
            .filter(new Filter.FilterBuilder().equals("baz", "bin").build())
            .select()
            .build();

    var expectedQueryParams = Optional.of(Map.of("baz", "eq.bin"));
    var actualQueryParams = deleteQuery.buildQueryParams();

    Assertions.assertTrue(actualQueryParams.isPresent());
    Assertions.assertEquals(expectedQueryParams, actualQueryParams);
  }

  @Test
  void testBuildAdditionalHeadersMinimal() {
    var deleteQuery =
        new DeleteQueryBuilder()
            .from("foo")
            .delete()
            .filter(new Filter.FilterBuilder().equals("baz", "bin").build())
            .build();

    var actualHeaders = deleteQuery.buildAdditionalHeaders();
    Assertions.assertEquals(Optional.empty(), actualHeaders);
  }

  @Test
  void testAddBuildAdditionalHeadersWithValues() {
    var deleteQuery =
        new DeleteQueryBuilder()
            .from("foo")
            .delete()
            .filter(new Filter.FilterBuilder().equals("baz", "bin").build())
            .select()
            .build();

    var actualHeaders = deleteQuery.buildAdditionalHeaders();
    Assertions.assertTrue(actualHeaders.isPresent());
    Assertions.assertEquals(HeaderType.RETRIEVE_RESPONSE_VALUES, actualHeaders.get());
  }

  @Test
  void testBuildRequestBody() {
    var deleteQuery =
        new DeleteQueryBuilder()
            .from("foo")
            .delete()
            .filter(new Filter.FilterBuilder().equals("baz", "bin").build())
            .build();
    var requestBody = deleteQuery.buildRequestBody();
    Assertions.assertTrue(requestBody.isEmpty());
  }
}
