package com.skhanal5.core.models;

import com.skhanal5.core.models.DeleteQuery.DeleteQueryBuilder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;

public class DeleteQueryTest {

    @Test
    void testBuildDeleteQueryMinimal() {
        Assertions.assertThrows(NullPointerException.class, () ->  new DeleteQuery.DeleteQueryBuilder().build());
    }

    @Test
    void testBuildDeleteQuery() {
        var expectedTable = "foo";
        var expectedSelect = true;
        var expectedFilter = new Filter.FilterBuilder().build();
        var expectedDelete = true;

        var deleteQuery = new DeleteQueryBuilder()
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
    void testConvertToQueryParamsWithValues() {
        var deleteQuery = new DeleteQueryBuilder()
                .from("foo")
                .delete()
                .filter(new Filter.FilterBuilder().equals("baz", "bin").build())
                .select()
                .build();

        var expectedQueryParams = new LinkedMultiValueMap<>();
        expectedQueryParams.put("baz", List.of("eq.bin"));

        var actualQueryParams = deleteQuery.convertToQueryParams();
        Assertions.assertEquals(expectedQueryParams, actualQueryParams);
    }

    @Test
    void testAddSelectHeaderMinimal() {
        var deleteQuery = new DeleteQueryBuilder()
                .from("foo")
                .delete()
                .filter(new Filter.FilterBuilder().equals("baz", "bin").build())
                .build();

        var actualHeaders = new HttpHeaders();
        var actualHeaderConsumer= deleteQuery.addSelectHeader();
        actualHeaderConsumer.accept(actualHeaders);

        Assertions.assertNotNull(actualHeaders);
        Assertions.assertTrue(actualHeaders.isEmpty());
    }

    @Test
    void testAddSelectHeaderWithValues() {
        var deleteQuery = new DeleteQueryBuilder()
                .from("foo")
                .delete()
                .filter(new Filter.FilterBuilder().equals("baz", "bin").build())
                .select()
                .build();

        var actualHeaders = new HttpHeaders();
        var actualHeaderConsumer= deleteQuery.addSelectHeader();
        actualHeaderConsumer.accept(actualHeaders);

        Assertions.assertNotNull(actualHeaders);
        Assertions.assertEquals(List.of("return=representation"), actualHeaders.get("Prefer"));
    }

}
