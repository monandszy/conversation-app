package code.frontend.exception;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

  @RequestMapping("/error")
  public String handleError(HttpServletRequest request, Model model) {
    Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
    Objects.requireNonNull(status);
    model.addAttribute("message", "catnip wor;ds %s".formatted(status));
    return "error";
  }

  @RequestMapping("/trigger")
  public void sendError() {
    throw new IllegalArgumentException("La messagium?");
  }

}