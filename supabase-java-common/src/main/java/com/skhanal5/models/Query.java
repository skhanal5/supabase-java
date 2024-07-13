package com.skhanal5.models;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public interface Query {
    public LinkedHashMap<String, List<String>> buildQueryParams();
    public HashMap<String, List<String>> buildAdditionalHeaders();
}
