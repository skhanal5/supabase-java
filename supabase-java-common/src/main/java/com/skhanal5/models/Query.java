package com.skhanal5.models;

import java.util.*;

public interface Query {

    public String getTable();
    public LinkedHashMap<String, List<String>> buildQueryParams();
    public Optional<Map<String, List<String>>> buildAdditionalHeaders();
}
