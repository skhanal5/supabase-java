package com.skhanal5.core.query;

import lombok.NonNull;
import lombok.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Value
public class InsertQuery {

    @NonNull
    String table;

    @NonNull
    List<Map<String,Object>> valuesToInsert;

    boolean select;

    private InsertQuery(@NonNull String table,
                        @NonNull List<Map<String, Object>> valuesToInsert,
                        boolean select) {
        this.table = table;
        this.valuesToInsert = valuesToInsert;
        this.select = select;
    }

    public static class InsertQueryBuilder {

        String table;

        List<Map<String, Object>> valuesToInsert;

        Boolean select = false;

        public InsertQueryBuilder from(String table) {
            this.table = table;
            return this;
        }

        @SafeVarargs
        public final InsertQueryBuilder insert(Map<String, Object>... value) {
            this.valuesToInsert = List.of(value);
            return this;
        }

        public InsertQueryBuilder select() {
            this.select = true;
            return this;
        }

        public InsertQuery build() {
            return new InsertQuery(table, valuesToInsert, select);
        }
    }

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
