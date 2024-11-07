package code.frontend.account;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
    super(authenticationManager);
    super.setAuthenticationFailureHandler((request, response, exception) ->
      response.sendRedirect("/login?invalid")
    ); // Setting this in SecurityConfig did not work even tho it initialized correctly
    super.setAuthenticationSuccessHandler((request, response, authentication) ->
      response.sendRedirect("/"));
    super.setUsernameParameter("email");
    super.setPasswordParameter("password");
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
    String email = request.getParameter("email");
    String password = request.getParameter("password");
    log.info("Attempting to authenticate user: [{}], [{}]", email, password);
    // not sus at all
    try {
      Authentication authResult = super.attemptAuthentication(request, response);
      SecurityContext context = SecurityContextHolder.getContext();
      context.setAuthentication(authResult);
      request.getSession().setAttribute(SPRING_SECURITY_CONTEXT_KEY, context);
      log.info("Account [{}] authenticated successfully - [{}]", email, authResult);
      return authResult;
    } catch (AuthenticationException e) {
      log.info("Authentication failed for account: [{}] - [{}]", email, e.getMessage());
      throw e; // rethrow to let Spring handle it
    }
  }

  @Override
  protected void successfulAuthentication(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain chain,
    Authentication authResult
  ) throws IOException, ServletException {
    super.successfulAuthentication(request, response, chain, authResult);
    log.info("Account [{}] logged in successfully", authResult);
  }

  @Override
  protected void unsuccessfulAuthentication(
    HttpServletRequest request, HttpServletResponse response, AuthenticationException failed
  ) throws IOException, ServletException {
    super.unsuccessfulAuthentication(request, response, failed);
    response.addHeader("message", failed.getMessage());
    log.info("Account login failed [{}]", failed.getMessage());
  }
}