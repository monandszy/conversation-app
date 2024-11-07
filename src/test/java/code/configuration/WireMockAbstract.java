package code.configuration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ClasspathFileSource;
import com.github.tomakehurst.wiremock.common.FileSource;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import lombok.Getter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.ActiveProfiles;

@Getter
@ActiveProfiles("test")
public abstract class WireMockAbstract {

  protected static WireMockServer wireMockServer;

  @BeforeAll
  static void beforeAll() {
    FileSource fs = new ClasspathFileSource("src/test/resources/wiremock/");
    wireMockServer = new WireMockServer(
      wireMockConfig()
        .port(9999)
        .fileSource(fs));
    wireMockServer.start();
  }

  @AfterAll
  static void afterAll() {
    wireMockServer.stop();
  }

  @AfterEach
  void afterEach() {
    wireMockServer.resetAll();
  }

}