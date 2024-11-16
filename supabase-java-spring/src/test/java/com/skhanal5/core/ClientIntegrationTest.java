package com.skhanal5.core;

import com.skhanal5.core.mockserver.MockServer;
import com.skhanal5.models.*;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ClientIntegrationTest {

  private static MockServer mockServer;

  private static final String baseUrl = "http://localhost:8080";

  @BeforeAll
  public static void setupServer() {
    mockServer = new MockServer();
    mockServer.registerSupabaseStubs();
    mockServer.start();
  }

  @AfterAll
  public static void stopServer() {
    mockServer.stop();
  }

  @Test
  void testExecuteSelect() {
    var client = SpringSupabaseClient.newInstance(baseUrl, "");
    var query = new SelectQuery.SelectQueryBuilder().select("").from("mocktable").build();
    var res = client.executeSelect(query, String.class);
    Assertions.assertEquals("[\"foo\":\"bar\"}]", res);
  }

  @Test
  void testExecuteInsert() {
    var client = SpringSupabaseClient.newInstance(baseUrl, "");
    var query =
        new InsertQuery.InsertQueryBuilder().insert(Map.of("bar", "baz")).from("mocktable").build();
    var res = client.executeInsert(query, String.class);
    Assertions.assertNull(res);
  }

  @Test
  void testExecuteDelete() {
    var client = SpringSupabaseClient.newInstance(baseUrl, "");
    var query =
        new DeleteQuery.DeleteQueryBuilder()
            .delete()
            .from("mocktable")
            .filter(new Filter.FilterBuilder().equals("foo", "bar").build())
            .build();
    var res = client.executeDelete(query, String.class);
    Assertions.assertNull(res);
  }

  @Test
  void testExecuteUpdate() {
    var client = SpringSupabaseClient.newInstance(baseUrl, "");
    var query =
        new UpdateQuery.UpdateQueryBuilder()
            .from("mocktable")
            .update(Map.of("foo", "var"))
            .filter(new Filter.FilterBuilder().equals("foo", "bar").build())
            .build();
    var res = client.executeUpdate(query, String.class);
    Assertions.assertNull(res);
  }
}
