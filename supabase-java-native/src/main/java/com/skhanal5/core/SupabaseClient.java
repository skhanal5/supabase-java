package com.skhanal5.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skhanal5.models.*;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import lombok.*;

/**
 * The main entry point to instantiation an instance of SupabaseClient and interact with Supabase
 * Database via REST. Utilizes HttpClient and Jackson under the hood to handle sending requests to
 * Supabase Database API and serializing responses.
 *
 * <p>We expose two ways of initializing an instance of SupabaseClient to consumers .
 *
 * @see #newInstance(String, String) Using the database url and service key
 * @see #newInstance(String, String, ObjectMapper) Using the databaes url, service key, and a
 *     Jackson ObjectMapper
 */
public class SupabaseClient {

  SupabaseHttpRequestSender sender;

  String baseURI;

  Map<String, String> defaultHeaders;

  private static final String ENDPOINT_PATH = "/rest/v1/";

  // correct usage of dependency injection
  SupabaseClient(SupabaseHttpRequestSender sender, String baseURI, Map<String,String> defaultHeaders) {
    this.sender = sender;
    this.baseURI = baseURI;
    this.defaultHeaders = defaultHeaders;
  }

//  private SupabaseClient(
//      HttpClient client, String baseURI, Map<String, String> defaultHeaders, ObjectMapper mapper) {
//    this.sender = new SupabaseHttpRequestSender(client, mapper);
//    this.baseURI = baseURI;
//    this.defaultHeaders = defaultHeaders;
//  }

  /**
   * Executes a SelectQuery and returns the search response as a POJO of type responseType. The
   * responseType class definition should match the schema of your table.
   *
   * @param query The query to execute
   * @param responseType The class of the POJO that you want the response to be converted to
   * @return the response POJO
   * @param <T> the type of the expected response POJO
   */
  public <T> T executeSelect(SelectQuery query, Class<T> responseType) {
//    try {
//      var request = new SupabaseHttpRequest(baseURI, defaultHeaders, query);
//      return sender.invokeRequest("GET", request, responseType).get();
//    } catch (JsonProcessingException | InterruptedException | ExecutionException e) {
//      throw new RuntimeException(e);
//    }
    return this.execute(query, responseType, "GET");
  }

  /**
   * Executes a InsertQuery and returns the inserted row as a POJO of type responseType. The
   * responseType class definition should match the schema of your table.
   *
   * <p>Note: Nothing will be returned, if select() is not explicitly invoked in your InsertQuery.
   * The method will return null.
   *
   * @param query The query to execute
   * @param responseType The class of the POJO that you want the response to be converted to
   * @return the response POJO or null
   * @param <T> the type of the expected response POJO
   */
  public <T> T executeInsert(InsertQuery query, Class<T> responseType) {
//    try {
//      var request = new SupabaseHttpRequest(baseURI, defaultHeaders, query);
//      return sender.invokeRequest("POST", request, responseType).get();
//    } catch (InterruptedException | ExecutionException | JsonProcessingException e) {
//      throw new RuntimeException(e);
//    }
    return this.execute(query, responseType, "POST");
  }

  /**
   * Executes a UpdateQuery and returns the updated row as a POJO of type responseType. The
   * responseType class definition should match the schema of your table.
   *
   * <p>Note: Nothing will be returned, if select() is not explicitly invoked in your UpdateQuery.
   * The method will return null.
   *
   * @param query The query to execute
   * @param responseType The class of the POJO that you want the response to be converted to
   * @return the response POJO or null
   * @param <T> the type of the expected response POJO
   */
  public <T> T executeUpdate(UpdateQuery query, Class<T> responseType) {
//    try {
//      var request = new SupabaseHttpRequest(baseURI, defaultHeaders, query);
//      return sender.invokeRequest("PATCH", request, responseType).get();
//    } catch (InterruptedException | ExecutionException | JsonProcessingException e) {
//      throw new RuntimeException(e);
//    }

    return this.execute(query, responseType, "PATCH");
  }

  /**
   * Executes a DeleteQuery and returns the deleted row as a POJO of type responseType. The
   * responseType class definition should match the schema of your table.
   *
   * <p>Note: Nothing will be returned, if select() is not explicitly invoked in your DeleteQuery.
   * The method will return null.
   *
   * @param query The query to execute
   * @param responseType The class of the POJO that you want the response to be converted to
   * @return the response POJO or null
   * @param <T> the type of the expected response POJO
   */
  public <T> T executeDelete(DeleteQuery query, Class<T> responseType) {
//    try {
//      var request = new SupabaseHttpRequest(baseURI, defaultHeaders, query);
//      return sender.invokeRequest("DELETE", request, responseType).get();
//    } catch (InterruptedException | ExecutionException | JsonProcessingException e) {
//      throw new RuntimeException(e);
//    }
    return this.execute(query, responseType, "DELETE");
  }

  <T> T execute(Query query, Class<T> responseType, String requestMethod) {
    try {
      var request = new SupabaseHttpRequest(baseURI, defaultHeaders, query);
      return sender.invokeRequest(requestMethod, request, responseType).get();
    } catch (InterruptedException | ExecutionException | JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * The most basic way of making an instance of the SupabaseClient. Utilizes built-in object mapper
   * that this library provides with no additional configuration.
   *
   * @param databaseUrl Represents the Supabase Database API base URL.
   * @param serviceKey Represents the Supabase Database service key. Note, this should not be
   *     exposed to clients.
   * @return an instance of a SupabaseClient
   */
  public static SupabaseClient newInstance(
      @NonNull String databaseUrl, @NonNull String serviceKey) {
    return newInstance(databaseUrl, serviceKey, new ObjectMapper());
  }

  /**
   * An alternative factory method to building a SupabaseClient. Allows consumers to provide their
   * own ObjectMapper in case there is a use case that requires the responseType POJO in database
   * operations to be deserialized in a specific manner.
   *
   * @param databaseUrl Represents the Supabase Database API base URL.
   * @param serviceKey Represents the Supabase Database service key. Note, this should not be
   *     exposed to clients.
   * @param mapper An instance of Jackson's ObjectMapper.
   * @return an instance of a SupabaseClient
   */
  public static SupabaseClient newInstance(
      @NonNull String databaseUrl, @NonNull String serviceKey, @NonNull ObjectMapper mapper) {
    var baseUrl = databaseUrl + ENDPOINT_PATH;
    var sender = new SupabaseHttpRequestSender(mapper);
    var clientHeaders =
        Map.ofEntries(
            Map.entry("apikey", serviceKey), Map.entry("Authorization", "Bearer " + serviceKey));

    return new SupabaseClient(sender, baseUrl, clientHeaders);
  }
}
