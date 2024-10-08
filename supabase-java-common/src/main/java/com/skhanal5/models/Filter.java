package com.skhanal5.models;

import com.skhanal5.constants.FilterType;
import java.util.*;
import java.util.Map.Entry;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

/**
 * Represents a Filter that is supported by Supabase Database API.
 *
 * @see <a href="https://supabase.com/docs/reference/javascript/using-filters">Supabase JavaScript's
 *     supported filters </a>
 *     <p>Note: not all database filters are supported by this library. It only covers the minimum
 *     functionality for filtering.
 */
@Value
@Getter(AccessLevel.PACKAGE)
public class Filter {

  Map<String, Map<String, Object>> filterData;

  private Filter(Map<String, Map<String, Object>> filterData) {
    this.filterData = filterData;
  }

  /**
   * The main entrypoint to build out a Filter instance based on the different operations you want
   * to include in your filter.
   */
  public static class FilterBuilder {

    Map<String, Map<String, Object>> filterData = new HashMap<>();

    /**
     * Filters out a column that equals a specific value in your table.
     *
     * @param column represents the column you want to filter
     * @param value represents the value you want the column to be equal to
     * @return A FilterBuilder with this filter configured
     */
    public FilterBuilder equals(String column, String value) {
      this.filterData.put(FilterType.EQUALS, Map.of(column, value));
      return this;
    }

    /**
     * Filters out a column that is greater than a specific value in your table.
     *
     * @param column represents the column you want to filter
     * @param value represents the value you want the column to be greater than
     * @return A FilterBuilder with this filter configured
     */
    public FilterBuilder greaterThan(String column, int value) {
      this.filterData.put(FilterType.GREATER_THAN, Map.of(column, value));
      return this;
    }

    /**
     * Filters out a column that is less than a specific value in your table.
     *
     * @param column represents the column you want to filter
     * @param value represents the value you want the column to be less than
     * @return A FilterBuilder with this filter configured
     */
    public FilterBuilder lessThan(String column, int value) {
      this.filterData.put(FilterType.LESS_THAN, Map.of(column, value));
      return this;
    }

    /**
     * Filters out a column that is greater than or equal to a specific value in your table.
     *
     * @param column represents the column you want to filter
     * @param value represents the value you want the column to be greater than or equal
     * @return A FilterBuilder with this filter configured
     */
    public FilterBuilder greaterThanOrEquals(String column, int value) {
      this.filterData.put(FilterType.GREATER_THAN_OR_EQUALS, Map.of(column, value));
      return this;
    }

    /**
     * Filters out a column that is less than or equal to a specific value in your table.
     *
     * @param column represents the column you want to filter
     * @param value represents the value you want the column to be less than or equal
     * @return A FilterBuilder with this filter configured
     */
    public FilterBuilder lessThanOrEquals(String column, int value) {
      this.filterData.put(FilterType.LESS_THAN_OR_EQUALS, Map.of(column, value));
      return this;
    }

    /**
     * Filters out a column that case sensitively matches a specific pattern
     *
     * @param column represents the column you want to filter
     * @param pattern represents the pattern you want the column to match.
     * @return A FilterBuilder with this filter configured
     */
    public FilterBuilder like(String column, String pattern) {
      this.filterData.put(FilterType.LIKE, Map.of(column, pattern));
      return this;
    }

    /**
     * Filters out a column that case insensitively matches a specific pattern
     *
     * @param column represents the column you want to filter
     * @param pattern represents the pattern you want the column to match.
     * @return A FilterBuilder with this filter configured
     */
    public FilterBuilder ilike(String column, String pattern) {
      this.filterData.put(FilterType.I_LIKE, Map.of(column, pattern));
      return this;
    }

    /**
     * Filters out a column that is either null or a boolean value
     *
     * @param column represents the column you want to filter
     * @param value represents null, true, or false
     * @return A FilterBuilder with this filter configured
     */
    public FilterBuilder is(String column, Optional<Boolean> value) {
      this.filterData.put(FilterType.IS, Map.of(column, value));
      return this;
    }

    /**
     * Filters out a column is one of the values included
     *
     * @param column represents the column you want to filter
     * @param values represents the potential values you want the column to be
     * @return A FilterBuilder with this filter configured
     */
    public FilterBuilder in(String column, List<String> values) {
      this.filterData.put(FilterType.IN, Map.of(column, values));
      return this;
    }

    /**
     * Filters out a column that is not equal to a specific value in your table.
     *
     * @param column represents the column you want to filter
     * @param value represents the value you want the column to not be equal to
     * @return A FilterBuilder with this filter configured
     */
    public FilterBuilder notEquals(String column, String value) {
      this.filterData.put(FilterType.NOT_EQUALS, Map.of(column, value));
      return this;
    }

    /**
     * Builds the Filter after configuring all column filters
     *
     * @return an instance of the Filter with all the filters included
     */
    public Filter build() {
      return new Filter(filterData);
    }
  }

  /**
   * Converts the Filter into query parameters that can be used by the client when sending a
   * request. <br>
   * <br>
   * Filters are represented as query parameters in the request to the Supabase Database API. It is
   * formatted as a key-value pair in the following format: (column, filterOperation.valueToFilter)
   *
   * @return a {@link Map} of query parameters, can be empty if there are no Filters
   */
  Map<String, String> convertFiltersToQueryParams() {
    var queryParams = new HashMap<String, String>();
    filterData.forEach(
        (filterType, filterColumnAndValue) -> {
          Entry<String, Object> columnToFilterValue =
              filterColumnAndValue.entrySet().iterator().next();
          var filterColumn = columnToFilterValue.getKey();
          var filterValue = columnToFilterValue.getValue();
          var filterValueString = stringifyFilterValue(filterValue);
          queryParams.put(filterColumn, filterType + filterValueString);
        });

    return queryParams;
  }

  private String stringifyFilterValue(Object filterValue) {
    if (filterValue instanceof List<?>) {
      List<String> list = (List<String>) filterValue;
      var stringifyList = String.join(",", list);
      return "(" + stringifyList + ")";
    } else {
      return filterValue.toString();
    }
  }
}
