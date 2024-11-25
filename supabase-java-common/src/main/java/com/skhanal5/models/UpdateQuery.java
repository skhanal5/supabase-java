package com.skhanal5.models;

import com.skhanal5.constants.HeaderType;
import java.util.*;
import lombok.NonNull;
import lombok.Value;

/**
 * Represents a query that is used to update rows from a Supabase Database table. <br>
 * <br>
 * For convenience, use the builder that is provided to provide the details of what table and rows
 * you want to update from.
 *
 * @see UpdateQueryBuilder
 */
@Value
public class UpdateQuery implements Query {

  @NonNull String table;

  @NonNull List<Map<String, Object>> valuesToUpdate;

  Boolean select;

  @NonNull Filter filter;

  private UpdateQuery(
      @NonNull String table,
      @NonNull List<Map<String, Object>> valuesToUpdate,
      @NonNull Filter filter,
      Boolean select) {
    this.table = table;
    this.valuesToUpdate = valuesToUpdate;
    this.filter = filter;
    this.select = select;
  }

  /**
   * The main entrypoint to build out a UpdateQuery instance based on the different settings you
   * want to configure in your query.
   */
  public static class UpdateQueryBuilder {

    String table;

    List<Map<String, Object>> valuesToUpdate;

    Filter filter;

    Boolean select = false;

    /**
     * Used to specify which table our query will update from
     *
     * @param table the name of the table of interest
     * @return a UpdateQueryBuilder with this configured
     */
    public UpdateQueryBuilder from(String table) {
      this.table = table;
      return this;
    }

    /**
     * Used to specify the values we want to update into the table.
     *
     * @param value a list of Map<String,Object> where each Map represents a row that we want to
     *     update. The key is the column name and the value is its corresponding value in the type
     *     that the column accepts.
     * @return a UpdateQueryBuilder with this configured
     */
    @SafeVarargs
    public final UpdateQueryBuilder update(Map<String, Object>... value) {
      this.valuesToUpdate = List.of(value);
      return this;
    }

    /**
     * Used to specify any Filters to refine our update query.
     *
     * @param filter A Filter that represents which can be used to specify which rows to select
     * @return a UpdateQueryBuilder with this configured
     */
    public UpdateQueryBuilder filter(Filter filter) {
      this.filter = filter;
      return this;
    }

    /**
     * Used in case the consumer wants to get the response back from the API with the updated
     * contents
     *
     * @return a UpdateQueryBuilder with this configured
     */
    public UpdateQueryBuilder select() {
      this.select = true;
      return this;
    }

    /**
     * Used to build a UpdateQuery with all configurations.
     *
     * @return an instance of the UpdateQuery
     */
    public UpdateQuery build() {
      return new UpdateQuery(table, valuesToUpdate, filter, select);
    }
  }

  /**
   * Converts this UpdateQuery into query parameters, so we can form a proper API call with the
   * correct parameters.
   *
   * @return A {@link Optional} that represents the query parameters and is consumed by the
   *     WebClient
   */
  @Override
  public Optional<Map<String, String>> buildQueryParams() {
    return Optional.of(filter.convertFiltersToQueryParams());
  }

  /**
   * In case select() is invoked, this method will add a header to the client that will return the
   * inserted contents in the response body.
   *
   * @return A {@link Optional} representing additional headers to pass in
   */
  @Override
  public Optional<Map<String, String>> buildAdditionalHeaders() {
    if (this.select) {
      return Optional.of(HeaderType.RETRIEVE_RESPONSE_VALUES);
    }
    return Optional.empty();
  }

  /**
   * Represents a request body.
   *
   * @return {@link Optional} since this UPDATE request doesn't have a body
   */
  @Override
  public Optional<List<Map<String, Object>>> buildRequestBody() {
    return Optional.of(valuesToUpdate);
  }
}
