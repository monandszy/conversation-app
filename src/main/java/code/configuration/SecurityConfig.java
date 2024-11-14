package code.configuration;

import code.frontend.account.CustomAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Slf4j
@Configuration
@AllArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

  private ObjectMapper mapper;
  private SpringTemplateEngine templateEngine;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public DaoAuthenticationProvider authProvider(
    UserDetailsService userDetailsService,
    PasswordEncoder passwordEncoder
  ) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setPasswordEncoder(passwordEncoder);
    provider.setUserDetailsService(userDetailsService);
    return provider;
  }

  @Bean
  public AuthenticationManager authManager(
    HttpSecurity http,
    AuthenticationProvider authProvider
  ) throws Exception {
    return http
      .getSharedObject(AuthenticationManagerBuilder.class)
      .authenticationProvider(authProvider)
      .build();
  }

  @Bean
  @SneakyThrows
  @ConditionalOnProperty(value = "spring.security.enabled",
    havingValue = "true", matchIfMissing = true)
  public SecurityFilterChain securityEnabled(
    HttpSecurity http,
    AuthenticationManager auth
  ) {
    return http
      .requestCache((cache) -> cache
        .requestCache(new NullRequestCache())
      )
      .authorizeHttpRequests(authorize -> authorize
        .requestMatchers(
          "/login",
          "/error",
          "/register",
          "/css/*",
          "/js/*",
          "/vendored/*",
          "favicon.ico"
        ).permitAll()
        .requestMatchers(
          "/catnip",
          "/catnip/*",
          "/"
        ).authenticated()
        .anyRequest().authenticated()
      )
      .formLogin(authorize -> authorize
        .loginPage("/login")
        .successForwardUrl("/conversation")
        .failureForwardUrl("/login?invalid")
        .usernameParameter("email")
        .permitAll()
      )
      .logout(authorize -> authorize
        .logoutSuccessUrl("/login?logout")
        .invalidateHttpSession(true)
        .deleteCookies("JSESSIONID")
        .permitAll()
      )
      .exceptionHandling(exh -> exh
        .authenticationEntryPoint((request, response, authException) -> {
          ServletOutputStream outputStream = response.getOutputStream();
          Map<String, Object> variables = new HashMap<>();
          if (authException instanceof InsufficientAuthenticationException) {
            response.sendRedirect("/login?unauthorized");
          } else {
            response.setContentType("text/html;charset=UTF-8");
            variables.put("message", authException.getMessage());
            flushView(request, variables, outputStream);
          }
        }))
      .addFilterBefore(new CustomAuthenticationFilter(auth), UsernamePasswordAuthenticationFilter.class)
      .build();
  }

  private void flushView(HttpServletRequest request, Map<String, Object> variables, ServletOutputStream outputStream) throws IOException {
    Context context = new Context();
    context.setVariables(variables);
    String renderedHtml;
    String hxRequest = request.getHeader("HX-Request");
    if (Objects.nonNull(hxRequest)) {
      renderedHtml = templateEngine.process("error :: fragment", context);
    } else {
      renderedHtml = templateEngine.process("error", context);
    }
    mapper.writeValue(outputStream, renderedHtml);
    outputStream.flush();
  }

  @Bean
  @ConditionalOnProperty(value = "spring.security.enabled", havingValue = "false")
  public SecurityFilterChain securityBypassed(HttpSecurity http) throws Exception {
    return http
      .csrf(AbstractHttpConfigurer::disable)
      .requestCache((cache) -> cache
        .requestCache(new NullRequestCache())
      )
      .authorizeHttpRequests(requests -> requests
        .anyRequest().permitAll()
      )
      .build();
  }

}