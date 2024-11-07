package code.configuration;

import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.dialect.IPostProcessorDialect;
import org.thymeleaf.engine.AbstractTemplateHandler;
import org.thymeleaf.model.IText;
import org.thymeleaf.postprocessor.IPostProcessor;
import org.thymeleaf.postprocessor.PostProcessor;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;

public class CustomTemplateProcessor implements IPostProcessorDialect {

  @Override
  public String getName() {
    return "BlankLinesPostProcessor";
  }

  @Override
  public Set<IPostProcessor> getPostProcessors() {
    Set<IPostProcessor> processors = new HashSet<>();
    processors.add(new PostProcessor(TemplateMode.HTML, CustomTemplateHandler.class, Integer.MAX_VALUE));
    return processors;
  }

  @Override
  public int getDialectPostProcessorPrecedence() {
    return 0;
  }

  public static class CustomTemplateHandler extends AbstractTemplateHandler {
    @Override
    public void handleText(IText text) {
      if (text.getText() != null && !text.getText().isEmpty() && text.getText().trim().isEmpty()) {
        return;
      } else {
        super.handleText(text);
      }
    }
  }

  @Configuration
  public static class ThymeleafConfig {
    SpringTemplateEngine templateEngine;
    @Autowired
    public ThymeleafConfig(SpringTemplateEngine templateEngine) {
      this.templateEngine = templateEngine;
      templateEngine.addDialect(new CustomTemplateProcessor());
    }
  }
}