package com.skhanal5.core.models;

import lombok.NonNull;
import lombok.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.function.Consumer;

/**
 * Represents a query that is used to select/search rows from a Supabase Database table.
 *
 * For convenience, use the builder that is provided to provide the details of
 * what table and rows you want to select from.
 * @see SelectQueryBuilder
 */
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

    /**
     * The main entrypoint to build out a SelectQuery instance based on the different
     * settings you want to configure in your query.
     */
    public static class SelectQueryBuilder {
        @NonNull
        String table;

        List<String> columnsToSelect;

        Pagination pagination;

        Filter filter;

        /**
         * Used to specify which table our query will select from
         * @param table the name of the table of interest
         * @return a SelectQueryBuilder with this configured
         */
        public SelectQueryBuilder from(String table) {
            this.table = table;
            return this;
        }

        /**
         * Used to specify which columns to select
         * @param columns a list of columns we want to select
         * @return a SelectQueryBuilder with this configured
         */
        public SelectQueryBuilder select(String... columns) {
            this.columnsToSelect = List.of(columns);
            return this;
        }

        /**
         * Used to specify how many rows we want to include in our query. This is
         * 0-based indexing (i.e., range(0,3) would include the first 4 results)
         *
         * @param start the first row we want to include in our search results
         * @param end the last row we want to include in our search results
         * @return a SelectQueryBuilder with this configured
         */
        public SelectQueryBuilder range(int start, int end) {
            this.pagination = new Pagination(start, end);
            return this;
        }

        /**
         * Used to specify any Filters to refine our select query.
         *
         * @param filter A Filter that represents which can be used to specify which rows to select
         * @return a InsertQueryBuilder with this configured
         */
        public SelectQueryBuilder filter(Filter filter) {
            this.filter = filter;
            return this;
        }

        /**
         * Used to build a InsertQuery with all configurations.
         *
         * @return an instance of the InsertQuery
         */
        public SelectQuery build() {
            return new SelectQuery(table,
                    columnsToSelect,
                    Optional.ofNullable(pagination),
                    Optional.ofNullable(filter));
        }

    }

    /**
     * Converts this SelectQuery into QueryParams, so we can form a proper
     * API call with the correct parameters.
     *
     * @return A MultiValueMap that represents the query parameters and is consumed by the WebClient
     */
    public MultiValueMap<String, String> convertToQueryParams() {
        var map = new LinkedHashMap<String, List<String>>();
        filter.ifPresent(filterVal -> filterVal.addFiltersOntoQueryParams(map));
        map.put("select", List.of(String.join(",", this.columnsToSelect)));
        return CollectionUtils.toMultiValueMap(map);
    }

    /**
     * In case range() is invoked, this method will add a header to the WebClient
     * that will include pagination.
     *
     * @return A Consumer<HttpHeaders> representing additional headers to pass in
     */
    public Consumer<HttpHeaders> addPaginationHeader() {
        var headers = new HashMap<String, List<String>>();
        this.pagination.ifPresent(paginationValue -> headers.put("Range", Collections.singletonList(paginationValue.serialize())));
        return bulkHeaders -> bulkHeaders.addAll(CollectionUtils.toMultiValueMap(headers));
    }
}
