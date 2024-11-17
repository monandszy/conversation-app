package code.frontend.account;

import static code.modules.accounts.AccountCommandFacade.AccountCreateDto;

import code.modules.accounts.AccountCommandFacade;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@AllArgsConstructor
public class RegistrationController {

  private AccountCommandFacade accountCommandFacade;
  private CustomAuthenticationFilter authenticationFilter;

  @GetMapping("/register")
  @ResponseStatus(HttpStatus.OK)
  String register(
    @RequestHeader(value = "HX-Request", required = false) String hxRequest,
    Model model
  ) {
    model.addAttribute("accountCreateDto", new AccountCreateDto("", ""));
    if (Objects.nonNull(hxRequest)) {
      return "authentication/register :: content";
    } else {
      return "authentication/register";
    }
  }

  @PostMapping("/register")
  RedirectView registerAccount(
    @ModelAttribute("accountCreateDto") AccountCreateDto account,
    HttpServletRequest request,
    HttpServletResponse response
  ) {
    accountCommandFacade.register(account);
    authenticationFilter.attemptAuthentication(request, response);
    return new RedirectView("/");
  }

}