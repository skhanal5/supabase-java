package com.skhanal5.models;

import java.util.*;

/**
 * A common interface that each query shares that is used for convenience when operating on queries
 * in the client.
 */
public interface Query {

  public String getTable();

  public Optional<Map<String, String>> buildQueryParams();

  public Optional<Map<String, String>> buildAdditionalHeaders();

  public Optional<List<Map<String, Object>>> buildRequestBody();
}
