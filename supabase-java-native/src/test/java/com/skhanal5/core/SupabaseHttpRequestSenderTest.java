package com.skhanal5.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SupabaseHttpRequestSenderTest {

    @Test
    public void testConstructorMinimal() {
        var sender = new SupabaseHttpRequestSender(null);
        Assertions.assertNotNull(sender.client);
        Assertions.assertNull(sender.mapper);
    }

    @Test
    public void testConstructorSetsCorrectValues() {
        var mapper = new ObjectMapper();
        var sender = new SupabaseHttpRequestSender(mapper);
        Assertions.assertEquals(mapper, sender.mapper);
        Assertions.assertNotNull(sender.client);
    }

//    @Test
//    public void testInvokeRequestGiven400Response() throws JsonProcessingException {
//        var client = mock(HttpClient.class);
//        var mapper = mock(ObjectMapper.class);
//        var request = mock(SupabaseHttpRequest.class);
//        HttpResponse<String> response = Mockito.mock(HttpResponse.class);
//        var sender = new SupabaseHttpRequestSender(client, mapper);
//        when(response.statusCode()).thenReturn(400);
//        when(client.sendAsync(any(), any())).thenReturn(CompletableFuture.completedFuture(response));
//
//        Assertions.assertNull(sender.invokeRequest("", request, String.class));
//    }

}
