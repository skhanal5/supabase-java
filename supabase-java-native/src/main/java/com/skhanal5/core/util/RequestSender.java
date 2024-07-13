package com.skhanal5.core;

import com.skhanal5.core.util.RequestBuilder;
import com.skhanal5.models.Query;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;

public class RequestSender {

    public RequestSender(Query query) {

    }

    public CompletionStage<HttpResponse<String>> sendRequest(String table,
                                                             Map<String, List<String>> queryParameters,
                                                             Map<String, List<String>> headers,
                                                             Class<T> responseType) {
        queryParameters.forEach(builder::addParameter);

        var request = HttpRequest
                .newBuilder()
                .uri(RequestBuilderHelper.buildURI(baseURI,table,queryParameters))
                .GET()
                .build();
    }

}
