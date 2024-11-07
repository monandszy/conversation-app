package code.configuration;

import code.openApi.ApiClient;
import code.openApi.infrastructure.ModelsApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleApiConfig {

  @Value("${api.generativelanguage.googleapis.com.url}")
  private String googleApiUrl;

  @Bean
  public ApiClient apiClient() {
    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath(googleApiUrl);
    return apiClient;
  }

  @Bean
  public ModelsApi getModelsApi(ApiClient apiClient) {
    return new ModelsApi(apiClient);
  }

}