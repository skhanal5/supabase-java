package com.skhanal5.core.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PaginationTest {

    @Test
    void testPaginationConstructor() {
        var pagination = new Pagination(0,0);
        Assertions.assertNotNull(pagination);
        Assertions.assertEquals(0, pagination.start);
        Assertions.assertEquals(0, pagination.end);
    }

    @Test
    void testSerialize() {
        var pagination = new Pagination(0,0);
        var expectedString = "0-0";
        Assertions.assertEquals(expectedString, pagination.serialize());
    }


}
