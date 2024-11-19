package code.frontend.conversation;

import code.configuration.Constants;
import code.modules.conversation.IConversationQueryFacade;
import code.modules.conversation.IConversationQueryFacade.ConversationData;
import code.modules.conversation.IConversationQueryFacade.RequestReadDto;
import code.modules.conversation.IConversationQueryFacade.ResponseReadDto;
import code.modules.conversation.IConversationQueryFacade.SectionReadDto;
import code.util.ControllerUtil;
import code.util.ModelAttr;
import java.security.Principal;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@AllArgsConstructor
@RequestMapping("/conversation")
@Slf4j
public class SectionPage implements ControllerUtil {

  private IConversationQueryFacade queryFacade;
  private ConversationPage conversationPage;

  @PreAuthorize("@ownershipValidator.validateConversation(principal, #conversationId)")
  @GetMapping("/{conversationId}")
  @ResponseStatus(HttpStatus.OK)
  String window(
    @RequestHeader(value = "HX-Request", required = false) String hxRequest,
    @RequestParam(defaultValue = "0") Integer page,
    @PathVariable UUID conversationId,
    Principal principal,
    Model model
  ) {
    PageRequest pageRequest = getPageRequest(page);
    Page<SectionReadDto> sectionPage = queryFacade.getSectionPage(pageRequest, conversationId);
    ConversationData data = queryFacade.getConversationData(conversationId);
    model.addAttribute(ModelAttr.selectedData, data);
    model.addAttribute(ModelAttr.sectionPage, sectionPage);
    model.addAttribute(ModelAttr.requestGenerateDto, getEmptyRequest());
    model.addAttribute(ModelAttr.conversationId, conversationId);
    model.addAttribute(ModelAttr.isHxRequest, hxRequest);
    if (Objects.nonNull(hxRequest)) {
      return "conversation/window :: fragment";
    } else {
      conversationPage.list(0, principal, model);
      return "conversation/window";
    }
  }

  @PreAuthorize("@ownershipValidator.validateConversation(principal, #conversationId)")
  @GetMapping("/{conversationId}/previous")
  @ResponseStatus(HttpStatus.OK)
  String windowPrevious(
    @RequestParam(defaultValue = "0") Integer page,
    @PathVariable UUID conversationId,
    Model model
  ) {
    PageRequest pageRequest = getPageRequest(page);
    Page<SectionReadDto> sectionPage = queryFacade.getSectionPage(pageRequest, conversationId);
    model.addAttribute(ModelAttr.sectionPage, sectionPage);
    model.addAttribute(ModelAttr.conversationId, conversationId);
    return "conversation/window-content :: previous-fragment";
  }

  @PreAuthorize("@ownershipValidator.validateConversation(principal, #conversationId)")
  @GetMapping("/{conversationId}/next")
  @ResponseStatus(HttpStatus.OK)
  String windowNext(
    @RequestParam(defaultValue = "0") Integer page,
    @PathVariable UUID conversationId,
    Model model
  ) {
    PageRequest pageRequest = getPageRequest(page);
    Page<SectionReadDto> sectionPage = queryFacade.getSectionPage(pageRequest, conversationId);
    model.addAttribute(ModelAttr.sectionPage, sectionPage);
    model.addAttribute(ModelAttr.conversationId, conversationId);
    return "conversation/window-content :: next-fragment";
  }

  @PreAuthorize("@ownershipValidator.validateRequest(principal, #requestId)")
  @GetMapping("/section/{sectionId}/request/{requestId}")
  @ResponseStatus(HttpStatus.OK)
  String getRequest(
    @PathVariable UUID sectionId,
    @PathVariable UUID requestId,
    Model model
  ) {
    RequestReadDto requestDto = queryFacade.getRequest(requestId, sectionId);
    model.addAttribute(ModelAttr.requestReadDto, requestDto);
    model.addAttribute(ModelAttr.sectionId, sectionId);
    return "conversation/window-content :: request-fragment";
  }

  @PreAuthorize("@ownershipValidator.validateResponse(principal, #responseId)")
  @GetMapping("/request/{requestId}/response/{responseId}")
  @ResponseStatus(HttpStatus.OK)
  String getResponse(
    @PathVariable UUID requestId,
    @PathVariable UUID responseId,
    Model model
  ) {
    ResponseReadDto responseDto = queryFacade.getResponse(responseId, requestId);
    model.addAttribute(ModelAttr.responseReadDto, responseDto);
    model.addAttribute(ModelAttr.requestId, requestId);
    return "conversation/window-content :: response-fragment";
  }

  private static PageRequest getPageRequest(Integer page) {
    return PageRequest.of(page, Constants.PAGE_SIZE);
  }
}