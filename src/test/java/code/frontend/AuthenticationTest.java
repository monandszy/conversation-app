package code.frontend;

import code.configuration.SecurityConfig;
import code.configuration.TestContainersConfig;
import code.configuration.UtilBeanConfig;
import code.frontend.account.AuthenticationController;
import code.frontend.account.AuthenticationController.LoginRequestDto;
import code.frontend.account.CustomAuthenticationFilter;
import code.frontend.account.RegistrationController;
import code.modules.accounts.AccountCommandFacade;
import code.modules.accounts.AccountCommandFacade.AccountCreateDto;
import code.modules.accounts.service.AuthService;
import code.util.TestFixtures;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.testcontainers.shaded.com.trilead.ssh2.auth.AuthenticationManager;

@WebMvcTest(controllers = {AuthenticationController.class, RegistrationController.class, CustomAuthenticationFilter.class})
@AutoConfigureMockMvc
@Import({SecurityConfig.class, UtilBeanConfig.class, TestContainersConfig.class})
@ActiveProfiles("test")
@AllArgsConstructor(onConstructor = @__(@Autowired))
class AuthenticationTest {

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.security.enabled", () -> "true");
  }

  private MockMvc mockMvc;

  @MockBean
  private AuthService authService;
  @MockBean
  private AccountCommandFacade accountCommandFacade;
  @MockBean
  private AuthenticationManager authenticationManager;

  @Test
  void should_secure_unauthorized() throws Exception {
    // TODO api security test
//    mockMvc.perform(get("/random"))
//      .andExpect(status().isUnauthorized());

    mockMvc.perform(MockMvcRequestBuilders.get("/random").accept(MediaType.TEXT_HTML))
      .andExpect(status().is3xxRedirection()) // Expect a 3xx status for redirection
      .andExpect(redirectedUrl("/login?unauthorized"));
  }

  @Test
  void should_return_login_view() throws Exception {
    LoginRequestDto expected = new LoginRequestDto("", "");
    mockMvc.perform(get("/login"))
      .andExpect(view().name("authentication/login"))
      .andExpect(model().attribute("loginRequestDto", expected))
      .andExpect(status().isOk());
  }

  @Test
  void should_return_register_view() throws Exception {
    AccountCreateDto expected = new AccountCreateDto("", "");
    mockMvc.perform(get("/register"))
      .andExpect(model().attribute("accountCreateDto", expected))
      .andExpect(view().name("authentication/register"))
      .andExpect(status().isOk());
  }

  @Test
  void should_register_account() throws Exception {
    AccountCreateDto createDto = TestFixtures.accountCreateDto;
    Mockito.when(accountCommandFacade.register(createDto))
      .thenReturn(null);
    Mockito.when(authService.loadUserByUsername(createDto.email()))
      .thenReturn(TestFixtures.user);
    mockMvc.perform(post("/register").with(csrf())
      .param("email", createDto.email())
      .param("password", createDto.password()));
    Mockito.verify(accountCommandFacade).register(createDto);
  }

  @Test
  void should_authenticate_account() throws Exception {
    LoginRequestDto loginDto = TestFixtures.loginRequestDto;
    Mockito.when(authService.loadUserByUsername(loginDto.email()))
      .thenReturn(TestFixtures.user);
    mockMvc.perform(post("/login").with(csrf())
      .param("email", loginDto.email())
      .param("password", loginDto.password()))
      .andExpect(status().is3xxRedirection())
      .andExpect(redirectedUrl("/conversation"));
  }
}