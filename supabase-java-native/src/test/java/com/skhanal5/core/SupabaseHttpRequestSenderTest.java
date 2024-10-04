package com.skhanal5.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.http.HttpResponse;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SupabaseHttpRequestSenderTest {

    @Test
    void testConstructorMinimal() {
        var sender = new SupabaseHttpRequestSender(null);
        Assertions.assertNotNull(sender.client);
        Assertions.assertNull(sender.mapper);
    }

    @Test
    void testConstructorSetsCorrectValues() {
        var mapper = new ObjectMapper();
        var sender = new SupabaseHttpRequestSender(mapper);
        Assertions.assertEquals(mapper, sender.mapper);
        Assertions.assertNotNull(sender.client);
    }

    @ParameterizedTest
    @ValueSource(ints = {200, 201, 299})
    void testValidateStatusCodeHappy(int statusCode) {
        var response = mock(HttpResponse.class);
        var sender = new SupabaseHttpRequestSender(new ObjectMapper());
        when(response.statusCode()).thenReturn(statusCode);
        Assertions.assertEquals(response, sender.validateStatusCode(response));
    }

}
