package code.frontend.conversation;

import code.configuration.Constants;
import code.modules.conversation.ConversationCommandFacade;
import code.modules.conversation.ConversationCommandFacade.RequestGenerateDto;
import code.modules.conversation.ConversationQueryFacade;
import code.modules.conversation.ConversationQueryFacade.RequestReadDto;
import code.modules.conversation.ConversationQueryFacade.ResponseReadDto;
import code.modules.conversation.ConversationQueryFacade.SectionReadDto;
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
public class SectionController implements ControllerUtil {

  private ConversationQueryFacade queryFacade;
  private ConversationCommandFacade commandFacade;
  private ConversationPage conversationPage;

  @GetMapping("/{conversationId}")
  @ResponseStatus(HttpStatus.OK)
  String window(
    @RequestHeader(value = "HX-Request", required = false) String hxRequest,
    @PathVariable String conversationId,
    Principal principal,
    Model model
  ) {
    PageRequest pageRequest = PageRequest.of(0, Constants.PAGE_SIZE, Sort.by("created").descending());
    Page<SectionReadDto> sectionPage = queryFacade.getSectionPage(pageRequest, UUID.fromString(conversationId));
    model.addAttribute("sectionPage", sectionPage);
    model.addAttribute("requestGenerateDto", getEmptyRequest());
    model.addAttribute("conversationId", conversationId);
    model.addAttribute("isHxRequest", hxRequest);
    if (Objects.nonNull(hxRequest)) {
      return "conversation/window :: fragment";
    } else {
      conversationPage.list(principal, model);
      return "conversation/window";
    }
  }

  @PostMapping("/{conversationId}")
  @ResponseStatus(HttpStatus.CREATED)
  String generate(
    @ModelAttribute RequestGenerateDto generateDto,
    @PathVariable String conversationId,
    Model model
  ) {
    SectionReadDto readDto = commandFacade
      .generate(generateDto, UUID.fromString(conversationId));
    model.addAttribute("sectionReadDto", readDto);
    return "conversation/window :: singular-fragment";
  }

  @PostMapping("/section/{sectionId}")
  @ResponseStatus(HttpStatus.CREATED)
  String regenerate(
    @ModelAttribute RequestGenerateDto generateDto,
    @PathVariable String sectionId,
    Model model
  ) {
    RequestReadDto readDto = commandFacade.regenerate(
      generateDto, UUID.fromString(sectionId));
    model.addAttribute("requestReadDto", readDto);
    model.addAttribute("sectionId", sectionId);
    return "conversation/window-content :: request-fragment";
  }

  @PostMapping("/request/{requestId}")
  @ResponseStatus(HttpStatus.CREATED)
  String retry(
    @PathVariable String requestId,
    Model model
  ) {
    ResponseReadDto readDto = commandFacade.retry(UUID.fromString(requestId));
    model.addAttribute("responseReadDto", readDto);
    model.addAttribute("requestId", requestId);
    return "conversation/window-content :: response-fragment";
  }

  @GetMapping("/section/{sectionId}/request/{requestId}")
  @ResponseStatus(HttpStatus.OK)
  String getRequest(
    @PathVariable String sectionId,
    @PathVariable String requestId,
    Model model
  ) {
    RequestReadDto requestDto = queryFacade.getRequest(
      UUID.fromString(requestId),
      UUID.fromString(sectionId)
    );
    model.addAttribute("requestReadDto", requestDto);
    model.addAttribute("sectionId", sectionId);
    return "conversation/window-content :: request-fragment";
  }

  @GetMapping("/request/{requestId}/response/{responseId}")
  @ResponseStatus(HttpStatus.OK)
  String getResponse(
    @PathVariable String requestId,
    @PathVariable String responseId,
    Model model
  ) {
    ResponseReadDto responseDto = queryFacade.getResponse(
      UUID.fromString(responseId),
      UUID.fromString(requestId)
    );
    model.addAttribute("responseReadDto", responseDto);
    model.addAttribute("requestId", requestId);
    return "conversation/window-content :: response-fragment";
  }
}