package com.skhanal5.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SupabaseClientTest {

  @Test
  void testNewInstanceURLAndServiceKeyMinimal1() {
    Assertions.assertThrows(NullPointerException.class, () -> SupabaseClient.newInstance("", null));
  }

  @Test
  void testNewInstanceURLAndServiceKeyMinimal2() {
    Assertions.assertThrows(NullPointerException.class, () -> SupabaseClient.newInstance(null, ""));
  }

  @Test
  void testNewInstanceURLAndServiceKey() {
    var url = "";
    var key = "";
    var supabaseClient = SupabaseClient.newInstance(url, key);
    Assertions.assertNotNull(supabaseClient.client);
  }
}
