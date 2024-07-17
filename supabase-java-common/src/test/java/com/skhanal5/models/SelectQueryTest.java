package com.skhanal5.models;

import com.skhanal5.models.SelectQuery.SelectQueryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class SelectQueryTest {

    @Test
    void testBuildSelectQueryMinimal() {
        Assertions.assertThrows(NullPointerException.class, () ->  new SelectQuery.SelectQueryBuilder().build());
    }

    @Test
    void testBuildSelectQueryWithValues() {
        var expectedTable = "foo";
        var expectedColumn = List.of("bar");
        var expectedStart = 0;
        var expectedEnd = 10;
        var expectedFilter = new Filter.FilterBuilder().build();

        var selectQuery = new SelectQuery
                .SelectQueryBuilder()
                .from(expectedTable)
                .select(expectedColumn.getFirst())
                .filter(expectedFilter)
                .range(0,10)
                .build();

        Assertions.assertEquals(expectedTable, selectQuery.getTable());
        Assertions.assertEquals(expectedColumn, selectQuery.getColumnsToSelect());
        Assertions.assertTrue(selectQuery.getPagination().isPresent());
        Assertions.assertEquals(expectedStart, selectQuery.getPagination().get().start);
        Assertions.assertEquals(expectedEnd, selectQuery.getPagination().get().end);
        Assertions.assertEquals(Optional.of(expectedFilter), selectQuery.getFilter());
    }

    @Test
    void testConvertToQueryParamsMinimal() {
        var selectQuery = new SelectQuery
                .SelectQueryBuilder()
                .from("foo")
                .select("bar")
                .build();

        var expectedQueryParams = Map.of("select", Collections.singletonList("bar"));
        var actualQueryParams = selectQuery.buildQueryParams();
        Assertions.assertEquals(expectedQueryParams, actualQueryParams);
    }

    @Test
    void testConvertToQueryParamsWithFilter() {
        var selectQuery = new SelectQueryBuilder()
                .from("foo")
                .select("bar")
                .filter(new Filter.FilterBuilder().equals("baz", "bin").build())
                .build();

        var expectedQueryParams = Map.of("baz", List.of("eq.bin"), "select", Collections.singletonList("bar"));

        var actualQueryParams = selectQuery.buildQueryParams();
        Assertions.assertEquals(expectedQueryParams, actualQueryParams);
    }

    @Test
    void testAddPaginationHeaderMinimal() {
        var selectQuery = new SelectQuery
                .SelectQueryBuilder()
                .from("foo")
                .select("bar")
                .build();

        var actualHeaders = selectQuery.buildAdditionalHeaders();

        Assertions.assertNotNull(actualHeaders);
        Assertions.assertTrue(actualHeaders.isEmpty());
    }

    @Test
    void testAddPaginationHeaderWithPagination() {
        var selectQuery = new SelectQuery
                .SelectQueryBuilder()
                .from("foo")
                .select("bar")
                .range(0,10)
                .build();

        var actualHeaders = selectQuery.buildAdditionalHeaders();

        Assertions.assertFalse(actualHeaders.isEmpty());
        Assertions.assertEquals(List.of("0-10"), actualHeaders.get().get("Range"));
    }
}
