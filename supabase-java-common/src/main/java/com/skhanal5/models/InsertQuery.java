package com.skhanal5.models;

import lombok.NonNull;
import lombok.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Represents a query that is used to insert rows from a Supabase Database table.
 *
 * For convenience, use the builder that is provided to provide the details of
 * what table and rows you want to insert from.
 * @see InsertQueryBuilder
 */
@Value
public class InsertQuery {

    @NonNull
    String table;

    @NonNull
    List<Map<String,Object>> valuesToInsert;

    Boolean select;

    /**
     * The main entrypoint to build out a InsertQuery instance based on the different
     * settings you want to configure in your query.
     */
    private InsertQuery(@NonNull String table,
                        @NonNull List<Map<String, Object>> valuesToInsert,
                        Boolean select) {
        this.table = table;
        this.valuesToInsert = valuesToInsert;
        this.select = select;
    }

    public static class InsertQueryBuilder {

        String table;

        List<Map<String, Object>> valuesToInsert;

        Boolean select = false;

        /**
         * Used to specify which table our query will insert into
         * @param table the name of the table of interest
         * @return a InsertQueryBuilder with this configured
         */
        public InsertQueryBuilder from(String table) {
            this.table = table;
            return this;
        }

        /**
         * Used to specify the values we want to insert into the table.
         * @param value a list of Map<String,Object> where each Map represents
         *              a row that we want to insert. The key is the column name
         *              and the value is its corresponding value in the type that
         *              the column accepts.
         * @return a InsertQueryBuilder with this configured
         */
        @SafeVarargs
        public final InsertQueryBuilder insert(Map<String, Object>... value) {
            this.valuesToInsert = List.of(value);
            return this;
        }

        /**
         * Used in case the consumer wants to get the response back from the API with
         * the inserted contents
         *
         * @return a InsertQueryBuilder with this configured
         */
        public InsertQueryBuilder select() {
            this.select = true;
            return this;
        }

        /**
         * Used to build a InsertQuery with all configurations.
         *
         * @return an instance of the InsertQuery
         */
        public InsertQuery build() {
            return new InsertQuery(table, valuesToInsert, select);
        }
    }

    /**
     * In case select() is invoked, this method will add a header to the WebClient
     * that will return the inserted contents in the response body.
     *
     * @return A Consumer<HttpHeaders> representing additional headers to pass in
     */
    public Consumer<HttpHeaders> addSelectHeader() {
        var headers = new HashMap<String, List<String>>();
        if (this.select) {
            headers.put("Prefer",List.of("return=representation")); //TODO: return only inserted values
        }
        return bulkHeaders -> {
            bulkHeaders.addAll(CollectionUtils.toMultiValueMap(headers));
        };
    }
}
