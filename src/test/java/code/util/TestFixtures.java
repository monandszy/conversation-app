package code.util;

import code.frontend.account.AuthenticationController.LoginRequestDto;
import code.modules.accounts.AccountCommandFacade.AccountCreateDto;
import code.modules.accounts.service.domain.AuthorityName;
import code.modules.catnips.CatnipCommandFacade.CatnipCreateDto;
import static code.modules.catnips.CatnipQueryFacade.CatnipReadDto;
import code.modules.catnips.service.Catnip;
import java.util.List;
import java.util.UUID;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

@UtilityClass
public class TestFixtures {
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