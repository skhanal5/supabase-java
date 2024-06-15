# supabase-java

## About
This project establishes a Java based client to interact with a Supabase database. Currently, Supabase does not offer a native
java client, so I tried to make one instead. My goal is to make a client that is based
in Spring as well as one that is non-Spring based for learning purposes.

## Project Specs
- Java 21

## Supabase Java Spring
A Supabase client that is native to the Spring 3 framework. It uses WebClient under
the hood to interact with the Supabase database API.

### Initialization

The easiest way of initalizing a SupabaseClient is by passing in the base URL of your Supabase database from your
dashboard along with the service key.

```dtd
    var client = SupabaseClient.newInstance("https://abcdefghijklmnop.supabase.co", SERVICE_KEY);
```

### Building a Query

From there, you can query your database tables by building a Query.
```dtd
    var query = new Query
        .QueryBuilder()
        .from("doctors")
        .select("*")
        .build();
```

This query is the SQL equivalent of selecting all fields
from the `doctors` table.

### Executing a Query

Lastly, you can execute the query and invoke the Supabase database API
by using `executeQuery(Query query, Class<T> responseClass)`. The `responseClass` parameter
is used to deserialize the JSON response into the corresponding POJO.

For example:
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

### Query Filters

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

```dtd
    var query = new Query.QueryBuilder()
            .from("doctors")
            .select("*")
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