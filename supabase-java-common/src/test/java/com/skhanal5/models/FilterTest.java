package com.skhanal5.models;

import com.skhanal5.constants.FilterType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
                FilterType.EQUALS,
                FilterType.GREATER_THAN,
                FilterType.LESS_THAN,
                FilterType.GREATER_THAN_OR_EQUALS,
                FilterType.LESS_THAN_OR_EQUALS,
                FilterType.LIKE,
                FilterType.I_LIKE,
                FilterType.IS,
                FilterType.IN,
                FilterType.NOT_EQUALS);
        for (String key: filterKeys) {
            Assertions.assertNotNull(filterData.get(key));
        }
    }

    @Test
    void testAddFiltersOntoQueryParamsMinimal() {
        var filter = new Filter.FilterBuilder().build();
        var queryParams = filter.convertFiltersToQueryParams();
        Assertions.assertTrue(queryParams.isEmpty());
    }

    @Test
    void testAddFiltersOntoQueryParamsWithValues() {
        var filter = new Filter.FilterBuilder()
                .equals("1", "")
                .greaterThan("2", 0)
                .build();
        var queryParams = filter.convertFiltersToQueryParams();
        Assertions.assertFalse(queryParams.isEmpty());
        Assertions.assertEquals(2, queryParams.size());
    }
}
