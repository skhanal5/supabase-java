package com.skhanal5.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skhanal5.models.Query;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SupabaseHttpRequestSenderTest {

    @Test
    public void testConstructorMinimal() {
        var sender = new SupabaseHttpRequestSender(null, null);
        Assertions.assertNull(sender.client);
        Assertions.assertNull(sender.mapper);
    }

    @Test
    public void testConstructorSetsCorrectValues() {
        var mapper = new ObjectMapper();
        var client = HttpClient.newBuilder().build();
        var sender = new SupabaseHttpRequestSender(client, mapper);
        Assertions.assertEquals(mapper, sender.mapper);
        Assertions.assertEquals(client, sender.client);
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
