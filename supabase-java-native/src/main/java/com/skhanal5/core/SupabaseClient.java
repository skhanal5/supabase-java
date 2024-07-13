package com.skhanal5.core;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.skhanal5.models.DeleteQuery;
import com.skhanal5.models.InsertQuery;
import com.skhanal5.models.SelectQuery;
import com.skhanal5.models.UpdateQuery;

import lombok.*;

import java.net.http.HttpClient;

/**
 * The main entry point to instantiation an instance of SupabaseClient and
 * interact with Supabase Database via REST. Utilizes WebClient and Jackson
 * under the hood to handle sending requests to Supabase Database API and
 * serializing responses.
 *
 * We expose two ways of initializing an instance of SupabaseClient to consumers .
 * @see #newInstance(String, String)    Using the database url and service key
 * @see #newInstance(String, String, ObjectMapper) Using the databaes url, service key, and a Jackson ObjectMapper
 */
public class SupabaseClient {

    @NonNull
    HttpClient client;

    @NonNull
    ObjectMapper mapper;

    String baseURI;

    private static final String ENDPOINT_PATH = "/rest/v1/";

    private SupabaseClient(String baseURI, HttpClient client) {
        this.baseURI = baseURI;
        this.client = client;
        this.mapper = new ObjectMapper();
    }

    private SupabaseClient(String baseURI, HttpClient client, ObjectMapper mapper) {
        this.baseURI = baseURI;
        this.client = client;
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
        // make RequestSender
        // return call
        var sender = new
        return this.makeSelectAPICall(query.getTable(),queryParams, additionalHeaders, responseType);
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
        var requestBody = query.getValuesToInsert();
        var additionalHeaders = query.buildAdditionalHeaders();;
        return this.makeInsertDBCall(query.getTable(), requestBody, additionalHeaders, responseType);
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
        var requestBody = query.getValuesToUpdate();
        var additionalHeaders = query.buildAdditionalHeaders();
        var queryParams = query.buildQueryParams();
        return this.makeUpdateDBCall(query.getTable(), additionalHeaders, queryParams, requestBody, responseType);
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
        var additionalHeaders = query.buildAdditionalHeaders();
        var queryParams = query.buildQueryParams();
        return this.makeDeleteAPICall(query.getTable(), additionalHeaders, queryParams, responseType);
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
        var baseUrl = databaseUrl + ENDPOINT_PATH;
        var client = HttpClient.newHttpClient();
        return new SupabaseClient(baseUrl, client);
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
        return new SupabaseClient(baseUrl, client, mapper);
    }
}
