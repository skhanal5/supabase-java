package com.skhanal5.models;

import java.util.*;

public interface Query {

    public String getTable();
    public Optional<Map<String, String>> buildQueryParams();
    public Optional<Map<String, String>> buildAdditionalHeaders();
    public Optional<List<Map<String, Object>>> buildRequestBody();
}
