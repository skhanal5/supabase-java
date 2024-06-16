package com.skhanal5.core.query;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;

@Value
public class SelectQuery {
    @NonNull
    String table;

    @NonNull
    List<String> columnsToSelect;

    Optional<Pagination> pagination;

    Optional<Filter> filter;

    private SelectQuery(@NonNull String table,
                        @NonNull List<String> columnsToSelect,
                        Optional<Pagination> pagination,
                        Optional<Filter> filter) {
        this.table = table;
        this.columnsToSelect = columnsToSelect;
        this.pagination = pagination;
        this.filter = filter;
    }

    public static class SelectQueryBuilder {
        @NonNull
        String table;

        List<String> columnsToSelect;

        Pagination pagination;

        Filter filter;

        public SelectQueryBuilder from(String table) {
            this.table = table;
            return this;
        }

        public SelectQueryBuilder select(String... columns) {
            this.columnsToSelect = List.of(columns);
            return this;
        }

        public SelectQueryBuilder range(int start, int end) {
            this.pagination = new Pagination(start, end);
            return this;
        }

        public SelectQueryBuilder filter(Filter filter) {
            this.filter = filter;
            return this;
        }

        public SelectQuery build() {
            return new SelectQuery(table,
                    columnsToSelect,
                    Optional.ofNullable(pagination),
                    Optional.of(filter));
        }

    }

    public MultiValueMap<String, String> convertToQueryParams() {
        var map = new LinkedHashMap<String, List<String>>();
        filter.ifPresent(filterVal -> filterVal.addFiltersOntoQueryParams(map));
        map.put("select", List.of(String.join(",", this.columnsToSelect)));
        return CollectionUtils.toMultiValueMap(map);
    }

    public Consumer<HttpHeaders> addHeaderValues() {
        var headers = new HashMap<String, List<String>>();
        this.pagination.ifPresent(paginationValue -> headers.put("Range", Collections.singletonList(paginationValue.serialize())));
        return bulkHeaders -> {
            bulkHeaders.addAll(CollectionUtils.toMultiValueMap(headers));
        };
    }
}
