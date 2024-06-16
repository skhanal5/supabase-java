package com.skhanal5.core.query;

import lombok.NonNull;
import lombok.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.function.Consumer;

@Value
public class UpdateQuery {

    @NonNull
    String table;

    @NonNull
    List<Map<String,Object>> valuesToUpdate;

    Boolean select;

    @NonNull
    Filter filter;

    private UpdateQuery(@NonNull String table,
                        @NonNull List<Map<String, Object>> valuesToUpdate,
                        @NonNull Filter filter,
                        Boolean select) {
        this.table = table;
        this.valuesToUpdate = valuesToUpdate;
        this.filter = filter;
        this.select = select;
    }

    public static class UpdateQueryBuilder {

        String table;

        List<Map<String, Object>> valuesToUpdate;

        Filter filter;

        Boolean select = false;

        public UpdateQueryBuilder from(String table) {
            this.table = table;
            return this;
        }

        @SafeVarargs
        public final UpdateQueryBuilder update(Map<String, Object>... value) {
            this.valuesToUpdate = List.of(value);
            return this;
        }

        public UpdateQueryBuilder filter(Filter filter) {
            this.filter = filter;
            return this;
        }

        public UpdateQueryBuilder select() {
            this.select = true;
            return this;
        }

        public UpdateQuery build() {
            return new UpdateQuery(table, valuesToUpdate, filter, select);
        }
    }

    public MultiValueMap<String, String> convertToQueryParams() {
        var map = new LinkedHashMap<String, List<String>>();
        filter.addFiltersOntoQueryParams(map);
        return CollectionUtils.toMultiValueMap(map);
    }

    public Consumer<HttpHeaders> addSelectHeader() {
        var headers = new HashMap<String, List<String>>();
        if (this.select) {
            headers.put("Prefer",List.of("return=representation")); //TODO: return only updated values
        }
        return bulkHeaders -> {
            bulkHeaders.addAll(CollectionUtils.toMultiValueMap(headers));
        };
    }
}
