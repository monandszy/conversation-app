package code.configuration;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;

@TestConfiguration
@ImportAutoConfiguration(exclude = SecurityAutoConfiguration.class)
public class TestSecurityConfig {

  @Bean
  public AuthenticationManager authManager() {
    // For CustomAuthenticationFilter compatability with disabled security
    return authentication -> null;
  }
}