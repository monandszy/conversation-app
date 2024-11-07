package code.configuration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testcontainers.shaded.com.google.common.net.HttpHeaders;

public interface WireMockTestSupport {

  default void stubForGenerateContent(final WireMockServer wireMockServer) {
    UrlPattern urlPattern = WireMock.urlPathEqualTo("/v1beta/models/gemini-1.5-flash:generateContent");
    wireMockServer.stubFor(WireMock.post(urlPattern)
      .willReturn(WireMock.aResponse()
        .withStatus(HttpStatus.OK.value())
        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
        .withBodyFile("stubForGenerateContent.json")
        .withTransformers("response-template"))
    );
  }
}