package com.skhanal5.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.mockito.Mockito.mock;

public class SupabaseClientTest {

    @Test
    void testNewInstanceClientOnlyMinimal() {
        Assertions.assertThrows(NullPointerException.class, () -> SupabaseClient.newInstance(null));
    }

    @Test
    void testNewInstanceClient() {
        var webClient = mock(WebClient.class);
        var supabaseClient = SupabaseClient.newInstance(webClient);
        Assertions.assertEquals(webClient, supabaseClient.client);
    }

    @Test
    void testNewInstanceClientAndMapperMinimal1() {
        var mapper = mock(ObjectMapper.class);
        Assertions.assertThrows(NullPointerException.class, () -> SupabaseClient.newInstance(null, mapper));
    }

    @Test
    void testNewInstanceClientAndMapperMinimal2() {
        var client = mock(WebClient.class);
        Assertions.assertThrows(NullPointerException.class, () -> SupabaseClient.newInstance(client, null));
    }

    @Test
    void testNewInstanceClientAndMapper() {
        var webClient = mock(WebClient.class);
        var mapper = mock(ObjectMapper.class);
        var supabaseClient = SupabaseClient.newInstance(webClient, mapper);
        Assertions.assertEquals(webClient, supabaseClient.client);
        Assertions.assertEquals(mapper, supabaseClient.mapper);
    }

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
