package com.skhanal5.core.query;

import lombok.Value;

import java.util.*;
import java.util.Map.Entry;


@Value
public class Filter {

    Map<String, Map<String, Object>> filterData;

    private Filter( Map<String, Map<String, Object>> filterData) {
        this.filterData = filterData;
    }

    public static class FilterBuilder {

        Map<String, Map<String, Object>> filterData = new HashMap<>();

        public FilterBuilder equals(String column, String value) {
            this.filterData.put("eq.", Map.of(column, value));
            return this;
        }

        public FilterBuilder greaterThan(String column, int value) {
            this.filterData.put("gt.", Map.of(column, value));
            return this;
        }

        public FilterBuilder lessThan(String column, int value) {
            this.filterData.put("lt.", Map.of(column, value));
            return this;
        }

        public FilterBuilder greaterThanOrEquals(String column, int value) {
            this.filterData.put("gte.", Map.of(column, value));
            return this;
        }

        public FilterBuilder lessThanOrEquals(String column, int value) {
            this.filterData.put("lte.", Map.of(column, value));
            return this;
        }
        public FilterBuilder like(String column, String pattern) {
            this.filterData.put("like.", Map.of(column, pattern));
            return this;
        }

        public FilterBuilder ilike(String column, String pattern) {
            this.filterData.put("ilike.", Map.of(column, pattern));
            return this;
        }

        public FilterBuilder is(String column, Optional<Boolean> value) {
            this.filterData.put("is.", Map.of(column, value));
            return this;
        }

        public FilterBuilder in(String column, List<String> values) {
            this.filterData.put("in.", Map.of(column, values));
            return this;
        }

        public FilterBuilder notEquals(String column, String value) {
            this.filterData.put("neq.", Map.of(column, value));
            return this;
        }

        public Filter build() {
            return new Filter(filterData);
        }
    }

    void addFiltersOntoQueryParams(LinkedHashMap<String, List<String>> queryParams) {
        filterData.forEach((filterType,filterColumnAndValue) -> {
            Entry<String, Object> columnToFilterValue = filterColumnAndValue.entrySet().iterator().next();
            var filterColumn = columnToFilterValue.getKey();
            var filterValue = columnToFilterValue.getValue();
            var filterValueStr = filterValue.toString();
            if (filterValue instanceof List<?>) {
                List<String> list = (List<String>) filterValue;
                var stringifyList = String.join(",", list);
                filterValueStr = "(" + stringifyList + ")";
            }
            queryParams.put(filterColumn, List.of(filterType + filterValueStr));
        });
    }

}
