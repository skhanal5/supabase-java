package com.skhanal5.core;


import lombok.NonNull;
import lombok.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import java.util.*;
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
            this.filterData.put("equals", Map.of(column, value));
            return this;
        }

        public QueryBuilder greaterThan(String column, int value) {
            this.filterData.put("greaterThan", Map.of(column, value));
            return this;
        }

        public QueryBuilder lessThan(String column, int value) {
            this.filterData.put("lessThan", Map.of(column, value));
            return this;
        }

        public QueryBuilder greaterThanEquals(String column, int value) {
            this.filterData.put("greaterThanEquals", Map.of(column, value));
            return this;
        }

        public QueryBuilder lessThanEquals(String column, int value) {
            this.filterData.put("lessThanEquals", Map.of(column, value));
            return this;
        }
        public QueryBuilder like(String column, String value) {
            this.filterData.put("like", Map.of(column, value));
            return this;
        }

        public QueryBuilder iLike(String column, String value) {
            this.filterData.put("iLike", Map.of(column, value));
            return this;
        }

        public QueryBuilder is(String column, Optional<Boolean> value) {
            this.filterData.put("is", Map.of(column, value));
            return this;
        }

        public QueryBuilder in(String column, List<String> values) {
            this.filterData.put("in", Map.of(column, values));
            return this;
        }

        public QueryBuilder notEqual(String column, String value) {
            this.filterData.put("notEqual", Map.of(column, value));
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
        var eqsOp = query.getFromFilter("equals");
        eqsOp.ifPresent(e -> {
            var entrySet = e.entrySet().iterator().next();
            queryParams.put(entrySet.getKey(), List.of("eq." + entrySet.getValue()));
        });

        var gtOp = query.getFromFilter("greaterThan");
        gtOp.ifPresent(e -> {
            var entrySet = e.entrySet().iterator().next();
            queryParams.put(entrySet.getKey(), List.of("gt." + entrySet.getValue()));
        });
    }

    private Optional<Map<String, Object>> getFromFilter(String key) {
        return this.filter.flatMap(filterValue -> {
            if (filterValue.get(key) == null) {
                return Optional.empty();
            } else {
                return Optional.of(filterValue.get(key));
            }
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
