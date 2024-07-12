package com.skhanal5.core.models;

import lombok.NonNull;
import lombok.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.function.Consumer;

/**
 * Represents a query that is used to delete rows from a Supabase Database table.
 *
 * For convenience, use the builder that is provided to provide the details of
 * what table and rows you want to delete from.
 * @see DeleteQueryBuilder
 */
@Value
public class DeleteQuery {

    @NonNull
    String table;

    @NonNull
    Boolean delete; //toggle functionality

    @NonNull
    Filter filter;

    Boolean select;

    private DeleteQuery(@NonNull String table,
                        @NonNull Boolean delete,
                        @NonNull Filter filter,
                        boolean select) {
        this.table = table;
        this.delete = delete;
        this.filter = filter;
        this.select = select;
    }

    /**
     * The main entrypoint to build out a DeleteQuery instance based on the different
     * settings you want to configure in your query.
     */
    public static class DeleteQueryBuilder {

        String table;

        Boolean delete;

        Filter filter;

        Boolean select = false;

        /**
         * Used to specify which table our query will delete from
         * @param table the name of the table of interest
         * @return a DeleteQueryBuilder with this configured
         */
        public DeleteQueryBuilder from(String table) {
            this.table = table;
            return this;
        }

        /**
         * Used to specify that we want to delete. A bit ambiguous, but included to
         * match the specification of how Supabase defined its JavaScript client.
         *
         * @return a DeleteQueryBuilder with this configured
         */
        public DeleteQueryBuilder delete() {
            this.delete = true;
            return this;
        }

        /**
         * Used to specify any Filters to refine our deletion query.
         *
         * @param filter A Filter that represents which can be used to specify which rows to delete
         * @return a DeleteQueryBuilder with this configured
         */
        public DeleteQueryBuilder filter(Filter filter) {
            this.filter = filter;
            return this;
        }

        /**
         * Used in case the consumer wants to get the response back from the API with
         * the deleted contents
         *
         * @return a DeleteQueryBuilder with this configured
         */
        public DeleteQueryBuilder select() {
            this.select = true;
            return this;
        }

        /**
         * Used to build a DeleteQuery with all configurations.
         *
         * @return an instance of the DeleteQuery
         */
        public DeleteQuery build() {
            return new DeleteQuery(table, delete, filter, select);
        }
    }

    /**
     * Converts this DeleteQuery into QueryParams, so we can form a proper
     * API call with the correct parameters.
     *
     * @return A MultiValueMap that represents the query parameters and is consumed by the WebClient
     */
    public MultiValueMap<String, String> convertToQueryParams() {
        var map = new LinkedHashMap<String, List<String>>();
        filter.addFiltersOntoQueryParams(map);
        return CollectionUtils.toMultiValueMap(map);
    }

    /**
     * In case select() is invoked, this method will add a header to the WebClient
     * that will return the deleted contents in the response body.
     *
     * @return A Consumer<HttpHeaders> representing additional headers to pass in
     */
    public Consumer<HttpHeaders> addSelectHeader() {
        var headers = new HashMap<String, List<String>>();
        if (this.select) {
            headers.put("Prefer",List.of("return=representation")); //TODO: return only deleted values
        }
        return bulkHeaders -> {
            bulkHeaders.addAll(CollectionUtils.toMultiValueMap(headers));
        };
    }
}
