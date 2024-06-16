# supabase-java

## About
This project establishes a Java based client to interact with a Supabase database. Currently, Supabase does not offer a
Java based library for its database, so I tried to make one instead.

### Supabase Java Native (*To be developed*)
A Supabase client library written in vanilla Java. It contains minimal dependencies to external libraries and makes use of 
Apache HTTPClient to interact with the Supabase database API. 

### Supabase Java Spring (*In development*)
A Supabase client that is native to the Spring 3 framework. It uses WebClient under
the hood to interact with the Supabase database API.

## Quick Start

### Initialization

The easiest way of initalizing a SupabaseClient is by passing in the base URL of your Supabase database from your
dashboard along with the service key.

```dtd
    var client = SupabaseClient.newInstance("https://abcdefghijklmnop.supabase.co", SERVICE_KEY);
```

### Building a Query

From there, you can invoke an operation on your database by building a query. Each operation
has its own corresponding query object.

#### SelectQuery

To select a column in the table, you can use the `SelectQuery`. For example:

```dtd
    var query = new SelectQuery
        .SelectQueryBuilder()
        .from("doctors")
        .select("*")
        .build();
```

This query is the SQL equivalent of selecting all fields
from the `doctors` table.

#### InsertQuery

To insert a row or multiple rows into the table, you can use the `InsertQuery`.

```dtd
    var query = new InsertQuery
        .InsertQueryBuilder()
        .from("doctors")
        .insert(Map.of("full_name", "John Doe")
        .select()
        .build();
```

Note: this method will return an empty string if you do not invoke select(). If select is invoked, it will
return the new row/rows inserted in the table.

#### UpdateQuery

If you want to update a specific row, you can use the `UpdateQuery` along with a `Filter`. For more information on
filtering, view the [section on using Filters](#Filters).

```dtd
    var query = new UpdateQuery
        .UpdateQueryBuilder()
        .from("doctors")
        .update(Map.of("full_name", "Sarah Smith")
        .filter(filter)
        .build();
```

Note: In addition, this query also will need the `select()` method explicitly invoked to get a proper String response.

### Executing a Query

Lastly, you can execute the query and invoke the Supabase database API
by using `executeQuery(Query query, Class<T> responseClass)`. The `responseClass` parameter
is used to deserialize the JSON response into the corresponding POJO.

If we wanted to execute the SearchQuery from above, we would do this:
```dtd
    var response = client.executeQuery(query, String.class);
    System.out.println(response);
    //output: [{"id":"12345-6789","foo":"bar"}]
```

#### Aside: Supabase Responses
As you can see, the underlying Supabase database produces a list of objects where each object
corresponds to a row of your table. The key-value pairs inside of that object map to the values under each
column defined in your table.

So the response above, would correspond to the table below:

|   | id (varchar) | foo (varchar) |
|---|--------------|---------------|
| 1 | 12345-6789   | bar           |


### Filters

You can include a Filter in your query to refine your search. Here are the following filters that are supported:

`equals(String column, String value)`
Match only rows where column is equal to value.

`notEquals(String column, String value)`
Match only rows where column is not equal to value.

`greaterThan(String column, int value)`
Match only rows where column is greater than value.

`greaterThanOrEquals(String column, int value)`
Match only rows where column is greater than or equal to value.

`lessThan(String column, int value)`
Match only rows where column is less than value.

`lessThanOrEquals(String column, int value)`
Match only rows where column is less than or equal to value.

`in(String column, List<String> values`
Match only rows where column contains every element appearing in value.

`like(String column, String pattern)`
Match only rows where column matches pattern case-sensitively.

`ilike(String column, String pattern)`
Match only rows where column matches pattern case-insensitively.

#### Query Filter Usage

Here is how you can use the FilterBuilder to build a Filter:

```dtd
    var filter = new Filter.FilterBuilder()
            .equals("specialty", "Internal Medicine")
            .greaterThan("years_of_experience", 2)
            .greaterThanOrEquals("years_of_experience", 5)
            .lessThan("distance", 2)
            .lessThanOrEquals("rating", 2)
            .in("state",List.of("Virginia", "Maryland"))
            .like("full_name", "%John Doe%")
            .ilike("phone_number", "%fakenumber%")
            .notEquals("full_name", "null")
            .build();
```

Note: Filters are supported for SearchQuery, UpdateQuery, and DeleteQuery

```dtd
    var query = new SearchQuery.SearchQueryBuilder()
            .from("doctors")
            .select("*")
            .filter(filter)
```