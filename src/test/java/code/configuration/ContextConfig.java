package code.configuration;

import code.modules.accounts.AccountCommandFacade;
import code.modules.conversation.IConversationQueryFacade;
import code.modules.google_api.GoogleApiAdapter;
import code.openApi.ApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

public class ContextConfig {

  @Configuration
  @ComponentScan(basePackageClasses = AccountCommandFacade.class)
  public static class AccountModuleContext {
    @Bean // Enables method validation
    public static MethodValidationPostProcessor methodValidationPostProcessor() {
      return new MethodValidationPostProcessor();
    }
  }

  @Configuration
  @ComponentScan(basePackageClasses = IConversationQueryFacade.class)
  public static class ConversationsModuleContext {
  }

  @Configuration
  @ComponentScan(basePackageClasses = {GoogleApiAdapter.class, ApiClient.class})
  public static class GoogleApiContext {}
}