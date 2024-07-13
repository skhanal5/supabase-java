package com.skhanal5.core;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletionStage;

public class ResponseHelper {

    HttpClient client;

    public CompletionStage<HttpResponse<String>> sendRequest(HttpRequest request) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

}
