package com.skhanal5.models;

import com.skhanal5.models.SelectQuery.SelectQueryBuilder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SelectQueryTest {

  @Test
  void testBuildSelectQueryMinimal() {
    Assertions.assertThrows(
        NullPointerException.class, () -> new SelectQuery.SelectQueryBuilder().build());
  }

  @Test
  void testBuildSelectQueryWithValues() {
    var expectedTable = "foo";
    var expectedColumn = List.of("bar");
    var expectedStart = 0;
    var expectedEnd = 10;
    var expectedFilter = new Filter.FilterBuilder().build();

    var selectQuery =
        new SelectQuery.SelectQueryBuilder()
            .from(expectedTable)
            .select(expectedColumn.getFirst())
            .filter(expectedFilter)
            .range(0, 10)
            .build();

    Assertions.assertEquals(expectedTable, selectQuery.getTable());
    Assertions.assertEquals(expectedColumn, selectQuery.getColumnsToSelect());
    Assertions.assertTrue(selectQuery.getPagination().isPresent());
    Assertions.assertEquals(expectedStart, selectQuery.getPagination().get().start);
    Assertions.assertEquals(expectedEnd, selectQuery.getPagination().get().end);
    Assertions.assertEquals(Optional.of(expectedFilter), selectQuery.getFilter());
  }

  @Test
  void testBuildQueryParamsMinimal() {
    var selectQuery = new SelectQuery.SelectQueryBuilder().from("foo").select("bar").build();

    var expectedQueryParams = Optional.of(Map.of("select", "bar"));

    var actualQueryParams = selectQuery.buildQueryParams();
    Assertions.assertTrue(actualQueryParams.isPresent());
    Assertions.assertEquals(expectedQueryParams, actualQueryParams);
  }

  @Test
  void testBuildQueryParamsWithValues() {
    var selectQuery =
        new SelectQueryBuilder()
            .from("foo")
            .select("bar")
            .filter(new Filter.FilterBuilder().equals("baz", "bin").build())
            .build();

    var expectedQueryParams = Optional.of(Map.of("baz", "eq.bin", "select", "bar"));

    var actualQueryParams = selectQuery.buildQueryParams();
    Assertions.assertTrue(actualQueryParams.isPresent());
    Assertions.assertEquals(expectedQueryParams, actualQueryParams);
  }

  @Test
  void testBuildAdditionalHeadersMinimal() {
    var selectQuery = new SelectQuery.SelectQueryBuilder().from("foo").select("bar").build();

    var actualHeaders = selectQuery.buildAdditionalHeaders();

    Assertions.assertNotNull(actualHeaders);
    Assertions.assertTrue(actualHeaders.isEmpty());
  }

  @Test
  void testBuildAdditionalHeadersWithValues() {
    var selectQuery =
        new SelectQuery.SelectQueryBuilder().from("foo").select("bar").range(0, 10).build();

    var actualHeaders = selectQuery.buildAdditionalHeaders();

    Assertions.assertTrue(actualHeaders.isPresent());
    Assertions.assertEquals("0-10", actualHeaders.get().get("Range"));
  }

  @Test
  void testBuildRequestBody() {
    var selectQuery =
        new SelectQuery.SelectQueryBuilder().from("foo").select("bar").range(0, 10).build();
    var requestBody = selectQuery.buildRequestBody();
    Assertions.assertTrue(requestBody.isEmpty());
  }
}
