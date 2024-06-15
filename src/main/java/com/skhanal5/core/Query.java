package com.skhanal5.core;


import lombok.NonNull;
import lombok.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;

@Value
public class Query {
    @NonNull
    String table;

    Optional<List<String>> columnsToSelect;

    Optional<Pagination> pagination;

    Optional<Map<String, Map<String, Object>>>  filter;

    private Query(String table, Optional<List<String>> columnsToSelect, Optional<Pagination> pagination,  Optional<Map<String, Map<String, Object>>> filter) {
        this.table = table;
        this.columnsToSelect = columnsToSelect;
        this.pagination = pagination;
        this.filter = filter;
    }

    public static class QueryBuilder {
        @NonNull
        String table;

        List<String> columnsToSelect;

        Pagination pagination;

        Map<String, Map<String, Object>> filterData = new HashMap<>();

        public QueryBuilder from(String table) {
            this.table = table;
            return this;
        }

        public QueryBuilder select(String... columns) {
            this.columnsToSelect = List.of(columns);
            return this;
        }

        public QueryBuilder range(int start, int end) {
            this.pagination = new Pagination(start, end);
            return this;
        }

        public QueryBuilder equals(String column, String value) {
            this.filterData.put("eq.", Map.of(column, value));
            return this;
        }

        public QueryBuilder greaterThan(String column, int value) {
            this.filterData.put("gt.", Map.of(column, value));
            return this;
        }

        public QueryBuilder lessThan(String column, int value) {
            this.filterData.put("lt.", Map.of(column, value));
            return this;
        }

        public QueryBuilder greaterThanOrEquals(String column, int value) {
            this.filterData.put("gte.", Map.of(column, value));
            return this;
        }

        public QueryBuilder lessThanOrEquals(String column, int value) {
            this.filterData.put("lte.", Map.of(column, value));
            return this;
        }
        public QueryBuilder like(String column, String pattern) {
            this.filterData.put("like.", Map.of(column, pattern));
            return this;
        }

        public QueryBuilder ilike(String column, String pattern) {
            this.filterData.put("ilike.", Map.of(column, pattern));
            return this;
        }

        public QueryBuilder is(String column, Optional<Boolean> value) {
            this.filterData.put("is.", Map.of(column, value));
            return this;
        }

        public QueryBuilder in(String column, List<String> values) {
            this.filterData.put("in.", Map.of(column, values));
            return this;
        }

        public QueryBuilder notEquals(String column, String value) {
            this.filterData.put("neq.", Map.of(column, value));
            return this;
        }

        public Query build() {
            return new Query(table, Optional.ofNullable(columnsToSelect),Optional.ofNullable(pagination), filterData.isEmpty() ? Optional.empty() : Optional.of(filterData));
        }

    }

    static MultiValueMap<String, String> convertToQueryParams(Query query) {
        var map = new LinkedHashMap<String, List<String>>();
        addFiltersOntoQueryParams(map, query);
        query.columnsToSelect.ifPresent(strings -> map.put("select", List.of(String.join(",", strings))));
        return CollectionUtils.toMultiValueMap(map);
    }

    private static void addFiltersOntoQueryParams(LinkedHashMap<String, List<String>> queryParams, Query query) {
        System.out.println(query.getFilter());
        var filterMap = query.getFilter();
        filterMap.ifPresent(filters -> {
            filters.forEach((filterType,filterColumnAndValue) -> {
                Entry<String, Object> columnToFilterValue = filterColumnAndValue.entrySet().iterator().next();
                var filterColumn = columnToFilterValue.getKey();
                var filterValue = columnToFilterValue.getValue();
                var filterValueStr = filterValue.toString();
                if (filterValue instanceof List<?>) {
                    List<String> list = (List<String>) filterValue;
                    var stringifyList = String.join(",", list);
                    filterValueStr = "(" + stringifyList + ")";
                }
                System.out.println(filterValueStr);
                queryParams.put(filterColumn, List.of(filterType + filterValueStr));
            });
        });
    }

    static Consumer<HttpHeaders> buildAdditionalHeaders(Query query) {
        var headers = new HashMap<String, List<String>>();
        query.pagination.ifPresent(paginationValue -> headers.put("Range", Collections.singletonList(paginationValue.serialize())));
        return bulkHeaders -> {
            bulkHeaders.addAll(CollectionUtils.toMultiValueMap(headers));
        };
    }
}
