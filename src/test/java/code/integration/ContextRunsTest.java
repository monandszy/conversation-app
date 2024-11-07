package code.integration;

import code.ConversationApp;
import code.configuration.TestContainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = ConversationApp.class)
@ActiveProfiles("test")
@Import(TestContainersConfig.class)
class ContextRunsTest {

  @Test
  void should_load() {
  }
}