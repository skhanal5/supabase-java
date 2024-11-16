package com.skhanal5.core.mockserver;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.github.tomakehurst.wiremock.WireMockServer;

public class MockServer {

  private final WireMockServer mockServer;

  public MockServer() {
    this.mockServer = new WireMockServer(options().port(8080));
  }

  public void registerSupabaseStubs() {
    this.stubSelectTable();
    this.stubInsertTable();
    this.stubDeleteTable();
    this.stubUpdateTable();
  }

  private void stubSelectTable() {
    mockServer.stubFor(
        get(urlPathMatching("/rest/v1/mocktable"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("[\"foo\":\"bar\"}]")));
  }

  private void stubInsertTable() {
    mockServer.stubFor(
        post(urlPathMatching("/rest/v1/mocktable"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("")));
  }

  private void stubDeleteTable() {
    mockServer.stubFor(
        delete(urlPathMatching("/rest/v1/mocktable"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("")));
  }

  private void stubUpdateTable() {
    mockServer.stubFor(
        patch(urlPathMatching("/rest/v1/mocktable"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("")));
  }

  public void start() {
    mockServer.start();
  }

  public void stop() {
    mockServer.stop();
  }
}
