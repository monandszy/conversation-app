package code.util;

import code.frontend.account.AuthenticationController.LoginRequestDto;
import code.modules.accounts.AccountCommandFacade;
import code.modules.accounts.service.domain.AuthorityName;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

@UtilityClass
public class TestFixtures {
  public AccountCommandFacade.AccountCreateDto accountCreateDto = new AccountCommandFacade.AccountCreateDto("email@email.com", "password");
  public List<AccountCommandFacade.AccountCreateDto> invalidAccountCreateDto = List.of(
    new AccountCommandFacade.AccountCreateDto(null, "password123"), // Invalid email (null)
    new AccountCommandFacade.AccountCreateDto("invalid-email", "pass"), // Invalid email and short password
    new AccountCommandFacade.AccountCreateDto("test@example.com", null), // Invalid password (null)
    new AccountCommandFacade.AccountCreateDto("test@example.com", ""), // Invalid password (blank)
    new AccountCommandFacade.AccountCreateDto("", "password123") // Invalid email (blank)
  );
  public LoginRequestDto loginRequestDto = new LoginRequestDto("email@email.com", "password");
  public UserDetails user = new User(
    "email@email.com",
      "$2a$10$oyzMxbxAJfeQs8txIeg7yeapbS1yglbM71LKrAwjF3lt9rXvkw5A6",
        true,
        true,
        true,
        true,
        List.of(new SimpleGrantedAuthority(AuthorityName.ROLE_USER.name())
    ));
}