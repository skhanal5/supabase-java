package com.skhanal5.core.query;

import lombok.NonNull;
import lombok.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.function.Consumer;

@Value
public class DeleteQuery {

    @NonNull
    String table;

    @NonNull
    Boolean delete; //toggle functionality

    @NonNull
    Filter filter;

    boolean select;

    private DeleteQuery(@NonNull String table,
                        @NonNull Boolean delete,
                        @NonNull Filter filter,
                        boolean select) {
        this.table = table;
        this.delete = delete;
        this.filter = filter;
        this.select = select;
    }

    public static class DeleteQueryBuilder {

        String table;

        Boolean delete;

        Filter filter;

        boolean select = false;

        public DeleteQueryBuilder from(String table) {
            this.table = table;
            return this;
        }

        public DeleteQueryBuilder delete() {
            this.delete = true;
            return this;
        }

        public DeleteQueryBuilder filter(Filter filter) {
            this.filter = filter;
            return this;
        }

        public DeleteQueryBuilder select() {
            this.select = true;
            return this;
        }

        public DeleteQuery build() {
            return new DeleteQuery(table, delete, filter, select);
        }
    }

    public MultiValueMap<String, String> convertToQueryParams() {
        var map = new LinkedHashMap<String, List<String>>();
        filter.addFiltersOntoQueryParams(map);
        return CollectionUtils.toMultiValueMap(map);
    }

    public Consumer<HttpHeaders> addDeleteHeader() {
        var headers = new HashMap<String, List<String>>();
        if (this.select) {
            headers.put("Prefer",List.of("return=representation")); //TODO: return only deleted values
        }
        return bulkHeaders -> {
            bulkHeaders.addAll(CollectionUtils.toMultiValueMap(headers));
        };
    }
}
