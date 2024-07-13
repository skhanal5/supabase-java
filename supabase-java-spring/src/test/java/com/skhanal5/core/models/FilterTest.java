package com.skhanal5.core.models;

import com.skhanal5.core.constants.FilterConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

class FilterTest {

    @Test
    void testFilterBuilderMinimal() {
        var filter = new Filter.FilterBuilder().build();
        Assertions.assertNotNull(filter);
        Assertions.assertTrue(filter.getFilterData().isEmpty());
    }

    @Test
    void testFilterBuilderWithAllFilters() {
        var filter = new Filter.FilterBuilder()
                .equals("", "")
                .greaterThan("", 0)
                .greaterThanOrEquals("", 0)
                .lessThan("", 0)
                .lessThanOrEquals("", 0)
                .in("", List.of(""))
                .is("", Optional.empty())
                .like("", "")
                .ilike("", "")
                .notEquals("", "")
                .build();
        assertFilterHasAllKeys(filter);
    }

    private void assertFilterHasAllKeys(Filter filter) {
        Assertions.assertNotNull(filter);
        var filterData = filter.getFilterData();
        Assertions.assertFalse(filterData.isEmpty());
        var filterKeys = List.of(
                FilterConstants.EQUALS,
                FilterConstants.GREATER_THAN,
                FilterConstants.LESS_THAN,
                FilterConstants.GREATER_THAN_OR_EQUALS,
                FilterConstants.LESS_THAN_OR_EQUALS,
                FilterConstants.LIKE,
                FilterConstants.I_LIKE,
                FilterConstants.IS,
                FilterConstants.IN,
                FilterConstants.NOT_EQUALS);
        for (String key: filterKeys) {
            Assertions.assertNotNull(filterData.get(key));
        }
    }

    @Test
    void testAddFiltersOntoQueryParamsMinimal() {
        LinkedHashMap<String, List<String>> queryParams = new LinkedHashMap<>();
        var filter = new Filter.FilterBuilder().build();
        filter.addFiltersOntoQueryParams(queryParams);
        Assertions.assertTrue(queryParams.isEmpty());
    }

    @Test
    void testAddFiltersOntoQueryParamsWithValues() {
        LinkedHashMap<String, List<String>> queryParams = new LinkedHashMap<>();
        var filter = new Filter.FilterBuilder()
                .equals("1", "")
                .greaterThan("2", 0)
                .build();
        filter.addFiltersOntoQueryParams(queryParams);
        Assertions.assertFalse(queryParams.isEmpty());
        Assertions.assertEquals(2, queryParams.size());
    }
}
