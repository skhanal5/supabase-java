package com.skhanal5.constants;

/**
 * Constants that represent each type of Filter. The constants
 * are defined according to <a href=https://supabase.com/docs/guides/api/sql-to-rest> Supabase SQL to REST </a>.
 */
public class FilterType {

    public static final String EQUALS = "eq.";

    public static final String GREATER_THAN = "gt.";

    public static final String LESS_THAN = "lt.";

    public static final String GREATER_THAN_OR_EQUALS = "gte.";

    public static final String LESS_THAN_OR_EQUALS = "lte.";

    public static final String LIKE = "like.";

    public static final String I_LIKE = "ilike.";

    public static final String IS = "is.";

    public static final String IN = "in.";

    public static final String NOT_EQUALS = "neq.";

    private FilterType() {
    }
}
