package com.skhanal5.core;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@WireMockTest
public class ClientIntegrationTest {

    @Test
    void testExecuteSelect() {

        stubFor(WireMock.get(urlEqualTo("")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody("")));

        var client = SupabaseClient.newInstance("http://localhost:8080","");

    }

}
