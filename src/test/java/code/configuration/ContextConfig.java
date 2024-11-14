package code.configuration;

import code.modules.accounts.AccountQueryFacade;
import code.modules.conversation.service.ConversationQueryFacade;
import code.modules.googleApi.GoogleApiAdapter;
import code.openApi.ApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

public class ContextConfig {

  @Configuration
  @ComponentScan(basePackageClasses = AccountQueryFacade.class)
  public static class AccountModuleContext {
    @Bean // Enables method validation
    public static MethodValidationPostProcessor methodValidationPostProcessor() {
      return new MethodValidationPostProcessor();
    }
  }

  @Configuration
  @ComponentScan(basePackageClasses = ConversationQueryFacade.class)
  public static class ConversationsModuleContext {
  }

  @Configuration
  @ComponentScan(basePackageClasses = {GoogleApiAdapter.class, ApiClient.class})
  public static class GoogleApiContext {}
}