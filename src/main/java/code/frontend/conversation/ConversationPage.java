package code.frontend.conversation;

import code.configuration.Constants;
import code.modules.conversation.IConversationCommandFacade;
import code.modules.conversation.IConversationCommandFacade.ConversationBeginDto;
import code.modules.conversation.IConversationCommandFacade.RequestGenerateDto;
import code.modules.conversation.IConversationQueryFacade;
import code.modules.conversation.IConversationQueryFacade.ConversationData;
import code.modules.conversation.IConversationQueryFacade.ConversationReadDto;
import code.util.ControllerUtil;
import code.util.ModelAttr;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@AllArgsConstructor
@RequestMapping("/conversation")
@Slf4j
public class ConversationPage implements ControllerUtil {

  private IConversationQueryFacade queryFacade;
  private IConversationCommandFacade commandFacade;

  @GetMapping("")
  @ResponseStatus(HttpStatus.OK)
  String index(
    @RequestHeader(value = "HX-Request", required = false) String hxRequest,
    Principal principal,
    Model model
  ) {
    list(0, principal, model);
    model.addAttribute(ModelAttr.requestGenerateDto, getEmptyRequest());
    model.addAttribute(ModelAttr.isHxRequest, hxRequest);
    if (Objects.nonNull(hxRequest)) {
      return "conversation/content :: fragment";
    } else {
      return "conversation/content";
    }
  }

  @GetMapping("/list")
  @ResponseStatus(HttpStatus.OK)
  String list(
    @RequestParam(defaultValue = "0") Integer page,
    Principal principal,
    Model model
  ) {
    PageRequest pageRequest = getPageRequest(page);
    UUID accountId = UUID.fromString(principal.getName());
    Page<ConversationReadDto> conversationPage =
      queryFacade.getConversationPage(pageRequest, accountId);
    model.addAttribute(ModelAttr.conversationPage, conversationPage);
    return "conversation/sidebar :: fragment";
  }

  @GetMapping("/list/next")
  public String getNext(
    @RequestParam Integer page,
    Principal principal,
    Model model
  ) {
    PageRequest pageRequest = getPageRequest(page);
    UUID accountId = UUID.fromString(principal.getName());
    Page<ConversationReadDto> conversationPage =
      queryFacade.getConversationPage(pageRequest, accountId);
    model.addAttribute(ModelAttr.conversationPage, conversationPage);
    return "conversation/sidebar-content :: next-fragment";
  }

  @GetMapping("/list/previous")
  public String getPrevious(
    @RequestParam Integer page,
    Principal principal,
    Model model
  ) {
    PageRequest pageRequest = getPageRequest(page);
    UUID accountId = UUID.fromString(principal.getName());
    Page<ConversationReadDto> conversationPage =
      queryFacade.getConversationPage(pageRequest, accountId);
    model.addAttribute(ModelAttr.conversationPage, conversationPage);

    return "conversation/sidebar-content :: previous-fragment";
  }

  @GetMapping("/introduction")
  @ResponseStatus(HttpStatus.OK)
  String introduction(
    Model model
  ) {
    model.addAttribute(ModelAttr.requestGenerateDto, getEmptyRequest());
    return "conversation/introduction-window :: fragment";
  }

  @GetMapping("/{conversationId}/header")
  @ResponseStatus(HttpStatus.OK)
  String header(
    @PathVariable String conversationId,
    Model model
  ) {
    ConversationData data = queryFacade.getConversationData(UUID.fromString(conversationId));
    model.addAttribute(ModelAttr.selectedData, data);
    return "conversation/window :: header-fragment";
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
    model.addAttribute(ModelAttr.conversationReadDto, readDto);
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

  private static PageRequest getPageRequest(Integer page) {
    return PageRequest.of(page, Constants.PAGE_SIZE, Sort.by("created").descending());
  }
}