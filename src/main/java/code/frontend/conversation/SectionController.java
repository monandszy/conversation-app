package code.frontend.conversation;

import code.configuration.Constants;
import code.modules.conversation.ConversationCommandFacade;
import code.modules.conversation.ConversationQueryFacade;
import code.modules.conversation.ConversationQueryFacade.ConversationReadDto;
import code.modules.conversation.ConversationQueryFacade.SectionReadDto;
import code.util.ControllerUtil;
import java.security.Principal;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    PageRequest pageRequest = PageRequest.of(0, Constants.PAGE_SIZE);
    ConversationReadDto readDto = new ConversationReadDto(UUID.fromString(conversationId));
    Page<SectionReadDto> sectionPage = queryFacade.getSectionPage(pageRequest, readDto);
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
  @ResponseStatus(HttpStatus.OK)
  String generate(
    @ModelAttribute ConversationCommandFacade.RequestGenerateDto generateDto,
    @PathVariable String conversationId,
    Model model
  ) {
    SectionReadDto readDto = commandFacade.generate(
      generateDto.withDependencyId(UUID.fromString(conversationId)));
    model.addAttribute("sectionReadDto", readDto);
    return "conversation/window :: singular-fragment";
  }
}