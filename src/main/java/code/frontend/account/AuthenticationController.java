package code.frontend.account;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@Controller
@AllArgsConstructor
public class AuthenticationController {

  @GetMapping("/login")
  @ResponseStatus(HttpStatus.OK)
  String login(
    @RequestHeader(value = "HX-Request", required = false) String hxRequest,
    Model model
  ) {
    model.addAttribute("loginRequestDto", new LoginRequestDto("", ""));
    if (Objects.nonNull(hxRequest)) {
      return "authentication/login :: content";
    } else {
      return "authentication/login";
    }
  }

  public record LoginRequestDto(String email, String password) {}

}