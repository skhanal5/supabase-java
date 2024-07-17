package com.skhanal5.models;

import com.skhanal5.models.UpdateQuery.UpdateQueryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class UpdateQueryTest {

    @Test
    void testBuildUpdateQueryMinimal() {
        Assertions.assertThrows(NullPointerException.class, () ->  new UpdateQuery.UpdateQueryBuilder().build());
    }

    @Test
    void testBuildUpdateQuery() {
        var expectedTable = "foo";
        var expectedSelect = true;
        List<Map<String,Object>> expectedValuesToUpdate = List.of(Map.of("foo", "bar"));
        var expectedFilter = new Filter.FilterBuilder().build();

        var updateQuery = new UpdateQueryBuilder()
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
    void testConvertToQueryParamsMinimal() {
        var updateQuery = new UpdateQueryBuilder()
                .from("foo")
                .update(Map.of("baz", "bar"))
                .select()
                .filter(new Filter.FilterBuilder().build())
                .build();

        var expectedQueryParams = Map.of();
        var actualQueryParams = updateQuery.buildQueryParams();
        Assertions.assertEquals(expectedQueryParams, actualQueryParams);
    }

    @Test
    void testConvertToQueryParamsWithValues() {
        var updateQuery = new UpdateQueryBuilder()
                .from("foo")
                .update(Map.of("baz", "bar"))
                .select()
                .filter(new Filter.FilterBuilder().equals("baz", "bin").build())
                .build();

        var expectedQueryParams = Map.of("baz", List.of("eq.bin"));

        var actualQueryParams = updateQuery.buildQueryParams();
        Assertions.assertEquals(expectedQueryParams, actualQueryParams);
    }

    @Test
    void testAddSelectHeaderMinimal() {
        var updateQuery = new UpdateQueryBuilder()
                .from("foo")
                .update(Map.of("baz", "bar"))
                .filter(new Filter.FilterBuilder().equals("baz", "bin").build())
                .build();

        var actualHeaders = updateQuery.buildAdditionalHeaders();

        Assertions.assertNotNull(actualHeaders);
        Assertions.assertTrue(actualHeaders.isEmpty());
    }

    @Test
    void testAddSelectHeaderWithValues() {
        var updateQuery = new UpdateQueryBuilder()
                .from("foo")
                .update(Map.of("baz", "bar"))
                .filter(new Filter.FilterBuilder().equals("baz", "bin").build())
                .select()
                .build();

        var actualHeaders = updateQuery.buildAdditionalHeaders();

        Assertions.assertFalse(actualHeaders.isEmpty());
        Assertions.assertEquals(List.of("return=representation"), actualHeaders.get().get("Prefer"));
    }
}
