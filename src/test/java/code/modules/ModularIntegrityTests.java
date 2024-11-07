package code.modules;

import code.ConversationApp;
import code.configuration.TestContainersConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;
import org.springframework.test.context.ActiveProfiles;

// https://spring.io/projects/spring-modulith
// https://docs.spring.io/spring-modulith/reference/
@Slf4j
@ActiveProfiles("test")
@SpringBootTest(classes = ConversationApp.class)
@Import(TestContainersConfig.class)
@AllArgsConstructor(onConstructor = @__(@Autowired))
class ModularIntegrityTests {
  private final ApplicationModules modules = ApplicationModules.of(ConversationApp.class);
  private ApplicationContext applicationContext;

  @Test
  void print_modules() {
    modules.forEach(System.out::println);
    modules.verify();
  }

  @Test
  void should_write_documentation() {
    new Documenter(modules, "build/reports/modulith-docs")
      .writeModuleCanvases(Documenter.CanvasOptions.defaults().revealInternals().revealEmptyLines());
  }

  @Test
  @Disabled
  void print_initialized_beans() {
    String[] beanNames = applicationContext.getBeanDefinitionNames();
    log.info("Beans initialized in the context:");
    for (String beanName : beanNames) {
      log.info(beanName);
    }
  }
}