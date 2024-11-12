package code.frontend.conversation;

import code.configuration.Constants;
import code.modules.conversation.ConversationCommandFacade;
import code.modules.conversation.ConversationCommandFacade.ConversationBeginDto;
import code.modules.conversation.ConversationCommandFacade.RequestGenerateDto;
import code.modules.conversation.ConversationQueryFacade;
import static code.modules.conversation.ConversationQueryFacade.ConversationReadDto;
import code.util.ControllerUtil;
import java.security.Principal;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@AllArgsConstructor
@RequestMapping("/conversation")
@Slf4j
public class ConversationPage implements ControllerUtil {

  private ConversationQueryFacade queryFacade;
  private ConversationCommandFacade commandFacade;

  @GetMapping("")
  @ResponseStatus(HttpStatus.OK)
  String index(
    @RequestHeader(value = "HX-Request", required = false) String hxRequest,
    Principal principal,
    Model model
  ) {
    list(principal, model);
    model.addAttribute("requestGenerateDto", getEmptyRequest());
    model.addAttribute("isHxRequest", hxRequest);
    if (Objects.nonNull(hxRequest)) {
      return "conversation/content :: fragment";
    } else {
      return "conversation/content";
    }
  }

  @GetMapping("/list")
  @ResponseStatus(HttpStatus.OK)
  String list(Principal principal, Model model) {
    PageRequest pageRequest = PageRequest.of(0, Constants.PAGE_SIZE, Sort.by("created").descending());
    UUID accountId = UUID.fromString(principal.getName());
    Page<ConversationReadDto> conversationPage = queryFacade.getConversationPage(pageRequest, accountId);
    model.addAttribute("conversationPage", conversationPage);
    return "conversation/sidebar :: fragment";
  }

  @GetMapping("/introduction")
  @ResponseStatus(HttpStatus.OK)
  String introduction(
    Model model
  ) {
    model.addAttribute("requestGenerateDto", getEmptyRequest());
    return "conversation/introduction-window :: fragment";
  }

  @PostMapping("")
  String beginConversation(
    @ModelAttribute RequestGenerateDto generateDto,
    Principal principal,
    Model model
  ) {
    ConversationBeginDto beginDto = new ConversationBeginDto(UUID.fromString(principal.getName()));
    ConversationReadDto readDto = commandFacade.begin(beginDto);
    commandFacade.generate(generateDto, readDto.id());
    model.addAttribute("conversationReadDto", readDto);
    return "conversation/sidebar :: singular-fragment";
  }

  @DeleteMapping("/{conversationId}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  String deleteConversation(
    @PathVariable String conversationId,
    Principal principal
  ) {
    commandFacade.deleteConversation(
      UUID.fromString(conversationId),
      UUID.fromString(principal.getName())
    );
    return "empty";
  }
}