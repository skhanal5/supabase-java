package com.skhanal5.models;

import com.skhanal5.models.InsertQuery.InsertQueryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class InsertQueryTest {

    @Test
    void testBuildInsertQueryMinimal() {
        Assertions.assertThrows(NullPointerException.class, () ->  new InsertQuery.InsertQueryBuilder().build());
    }

    @Test
    void testBuildInsertQuery() {
        var expectedTable = "foo";
        var expectedSelect = true;
        List<Map<String,Object>> expectedInsert = List.of(Map.of("foo", "bar"));

        var insertQuery = new InsertQueryBuilder()
                .from(expectedTable)
                .insert(expectedInsert.getFirst())
                .select()
                .build();

        Assertions.assertEquals(expectedTable, insertQuery.getTable());
        Assertions.assertEquals(expectedSelect, insertQuery.getSelect());
        Assertions.assertEquals(expectedInsert, insertQuery.getValuesToInsert());
    }

    @Test
    void testAddSelectHeaderMinimal() {
        var insertQuery = new InsertQuery
                .InsertQueryBuilder()
                .from("foo")
                .insert(Map.of("bar", "baz"))
                .build();

        var actualHeaders = insertQuery.buildAdditionalHeaders();

        Assertions.assertNotNull(actualHeaders);
        Assertions.assertTrue(actualHeaders.isEmpty());
    }

    @Test
    void testAddSelectHeaderWithValues() {
        var insertQuery = new InsertQueryBuilder()
                .from("foo")
                .insert(Map.of("bar", "baz"))
                .select()
                .build();

        var actualHeaders = insertQuery.buildAdditionalHeaders();
        Assertions.assertNotNull(actualHeaders);
        Assertions.assertEquals(List.of("return=representation"), actualHeaders.get("Prefer"));
    }
}
