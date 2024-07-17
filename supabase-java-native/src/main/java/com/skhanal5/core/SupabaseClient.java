package com.skhanal5.core;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.skhanal5.core.internal.SupabaseHttpRequestSender;
import com.skhanal5.models.DeleteQuery;
import com.skhanal5.models.InsertQuery;
import com.skhanal5.models.SelectQuery;
import com.skhanal5.models.UpdateQuery;

import lombok.*;

import java.net.http.HttpClient;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * The main entry point to instantiation an instance of SupabaseClient and
 * interact with Supabase Database via REST. Utilizes HttpClient and Jackson
 * under the hood to handle sending requests to Supabase Database API and
 * serializing responses.
 *
 * We expose two ways of initializing an instance of SupabaseClient to consumers .
 * @see #newInstance(String, String)    Using the database url and service key
 * @see #newInstance(String, String, ObjectMapper) Using the databaes url, service key, and a Jackson ObjectMapper
 */
public class SupabaseClient {

    HttpClient client;

    String baseURI;

    ObjectMapper mapper;

    Map<String,String> defaultHeaders;

    private static final String ENDPOINT_PATH = "/rest/v1/";

    private SupabaseClient(HttpClient client, String baseURI, Map<String,String> defaultHeaders, ObjectMapper mapper) {
        this.client = client;
        this.baseURI = baseURI;
        this.defaultHeaders = defaultHeaders;
        this.mapper = mapper;
    }

    /**
     * Executes a SelectQuery and returns the search response as a POJO of type responseType. The responseType
     * class definition should match the schema of your table.
     *
     * @param query The query to execute
     * @param responseType The class of the POJO that you want the response to be converted to
     * @return  the response POJO
     * @param <T> the type of the expected response POJO
     */
    public <T> T executeSelect(SelectQuery query, Class<T> responseType) {
        var sender = new SupabaseHttpRequestSender<>(baseURI, client, defaultHeaders, query, responseType, mapper);
        try {
            return sender.invokeGETRequest().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Executes a InsertQuery and returns the inserted row as a POJO of type responseType. The responseType
     * class definition should match the schema of your table.
     *
     * Note: Nothing will be returned, if select() is not explicitly invoked in your InsertQuery. The method
     * will return null.
     *
     * @param query The query to execute
     * @param responseType The class of the POJO that you want the response to be converted to
     * @return  the response POJO or null
     * @param <T> the type of the expected response POJO
     */
    public <T> T executeInsert(InsertQuery query, Class<T> responseType) {
        var sender = new SupabaseHttpRequestSender<>(baseURI, client, defaultHeaders, query, responseType, mapper);
        try {
            return sender.invokePOSTRequest().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Executes a UpdateQuery and returns the updated row as a POJO of type responseType. The responseType
     * class definition should match the schema of your table.
     *
     * Note: Nothing will be returned, if select() is not explicitly invoked in your UpdateQuery. The method
     * will return null.
     *
     * @param query The query to execute
     * @param responseType The class of the POJO that you want the response to be converted to
     * @return  the response POJO or null
     * @param <T> the type of the expected response POJO
     */
    public <T> T executeUpdate(UpdateQuery query, Class<T> responseType) {
        var sender = new SupabaseHttpRequestSender<>(baseURI, client, defaultHeaders, query, responseType, mapper);
        try {
            return sender.invokePATCHRequest().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Executes a DeleteQuery and returns the deleted row as a POJO of type responseType. The responseType
     * class definition should match the schema of your table.
     *
     * Note: Nothing will be returned, if select() is not explicitly invoked in your DeleteQuery. The method
     * will return null.
     *
     * @param query The query to execute
     * @param responseType The class of the POJO that you want the response to be converted to
     * @return  the response POJO or null
     * @param <T> the type of the expected response POJO
     */
    public <T> T executeDelete(DeleteQuery query, Class<T> responseType) {
        var sender = new SupabaseHttpRequestSender<>(baseURI, client, defaultHeaders, query, responseType, mapper);
        try {
            return sender.invokeDELETERequest().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * The most basic way of making an instance of the SupabaseClient. Utilizes built-in object mapper that
     * this library provides with no additional configuration.
     *
     * @param databaseUrl Represents the Supabase Database API base URL.
     * @param serviceKey Represents the Supabase Database service key. Note, this should not be exposed to clients.
     * @return an instance of a SupabaseClient
     */
    public static SupabaseClient newInstance(@NonNull String databaseUrl, @NonNull String serviceKey) {
       return newInstance(databaseUrl, serviceKey, new ObjectMapper());
    }

    /**
     * An alternative factory method to building a SupabaseClient. Allows consumers to provide their own ObjectMapper
     * in case there is a use case that requires the responseType POJO in database operations to be deserialized in a
     * specific manner.
     *
     * @param databaseUrl Represents the Supabase Database API base URL.
     * @param serviceKey Represents the Supabase Database service key. Note, this should not be exposed to clients.
     * @param mapper An instance of Jackson's ObjectMapper.
     * @return an instance of a SupabaseClient
     */
    public static SupabaseClient newInstance(@NonNull String databaseUrl, @NonNull String serviceKey, @NonNull ObjectMapper mapper) {
        var baseUrl = databaseUrl + ENDPOINT_PATH;
        var client = HttpClient.newHttpClient();
        var clientHeaders = Map.ofEntries(
                Map.entry("apikey", serviceKey),
                Map.entry("Authorization", "Bearer " + serviceKey)
        );
        return new SupabaseClient(client, baseUrl, clientHeaders, mapper);
    }
}
