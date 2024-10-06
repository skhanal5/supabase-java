package com.skhanal5.core;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;

@WireMockTest
public class ClientIntegrationTest {

  @Test
  void testExecuteSelect() {

    stubFor(
        WireMock.get(urlEqualTo(""))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("")));

    var client = SupabaseClient.newInstance("http://localhost:8080", "");
  }
}
