package code.api;

import code.configuration.ContextConfig;
import code.configuration.GoogleApiConfig;
import code.configuration.TestSecurityConfig;
import code.configuration.WireMockAbstract;
import code.configuration.WireMockTestSupport;
import code.modules.googleApi.GoogleApiAdapter;
import code.modules.googleApi.GoogleApiAdapter.ApiResponseDto;
import code.util.EmptyController;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@WebMvcTest(controllers = EmptyController.class)
@Import({ContextConfig.GoogleApiContext.class, GoogleApiConfig.class, TestSecurityConfig.class})
@AllArgsConstructor(onConstructor = @__(@Autowired))
class GoogleApiAdapterTest extends WireMockAbstract implements WireMockTestSupport {

  private GoogleApiAdapter googleApiAdapter;

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("api.generativelanguage.googleapis.com.url", () -> "http://localhost:9999/");
  }

  @Test
  void should_generate() {
    stubForGenerateContent(wireMockServer);
    // given
    String text = "query";
    // when
    ApiResponseDto generate = googleApiAdapter.generate(new GoogleApiAdapter.ApiRequestDto(text));
    // then
    assertNotNull(generate);
    Assertions.assertEquals(generate.text(), "EXPECTED_TEXT");
  }
}