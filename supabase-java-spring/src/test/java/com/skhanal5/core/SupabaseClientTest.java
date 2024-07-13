package com.skhanal5.core;

import com.fasterxml.jackson.databind.ObjectMapper;
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
        Assertions.assertNotNull(supabaseClient.mapper);
    }

    @Test
    void testNewInstanceURLKeyAndMapperMinimal() {
        var url = "";
        var key = "";
        Assertions.assertThrows(NullPointerException.class, () -> SupabaseClient.newInstance(url, key, null));
    }

    @Test
    void testNewInstanceURLKeyAndMapper() {
        var url = "";
        var key = "";
        var mapper = new ObjectMapper();
        var supabaseClient = SupabaseClient.newInstance(url,key,mapper);
        Assertions.assertNotNull(supabaseClient);
        Assertions.assertNotNull(supabaseClient.client);
        Assertions.assertEquals(mapper, supabaseClient.mapper);
    }
}
