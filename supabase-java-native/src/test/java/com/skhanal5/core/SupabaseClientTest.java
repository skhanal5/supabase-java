package com.skhanal5.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class SupabaseClientTest {

    @Test
    void testNewInstanceGivenNullInputs() {
        Assertions.assertThrows(NullPointerException.class, () -> SupabaseClient.newInstance(null, null));
    }

    @Test
    void testNewInstanceAlternateGivenNullInputs() {
        Assertions.assertThrows(NullPointerException.class, () -> SupabaseClient.newInstance(null, null, null));
    }

    @Test
    void testNewInstanceWithURLAndServiceKey() {
        var client = SupabaseClient.newInstance("","");
        var expectedHeaders = Map.of("apikey", "", "Authorization", "Bearer ");
        Assertions.assertEquals("/rest/v1/", client.baseURI);
        Assertions.assertNotNull(client.sender);
        Assertions.assertEquals(expectedHeaders, client.defaultHeaders);
    }

    @Test
    void testNewInstanceWithURLServiceKeyAndMapper() {
        var client = SupabaseClient.newInstance("","");
        var expectedHeaders = Map.of("apikey", "", "Authorization", "Bearer ");
        Assertions.assertEquals("/rest/v1/", client.baseURI);
        Assertions.assertNotNull(client.sender);
        Assertions.assertEquals(expectedHeaders, client.defaultHeaders);
    }
}
