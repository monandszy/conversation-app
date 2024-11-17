package code.frontend.conversation;

import code.configuration.Constants;
import code.modules.conversation.IConversationCommandFacade;
import code.modules.conversation.IConversationCommandFacade.RequestGenerateDto;
import code.modules.conversation.IConversationQueryFacade;
import code.modules.conversation.IConversationQueryFacade.ConversationData;
import code.modules.conversation.IConversationQueryFacade.ConversationReadDto;
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
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class SectionController implements ControllerUtil {

  private IConversationQueryFacade queryFacade;
  private IConversationCommandFacade commandFacade;
  private ConversationPage conversationPage;

  @GetMapping("/{conversationId}")
  @ResponseStatus(HttpStatus.OK)
  String window(
    @RequestHeader(value = "HX-Request", required = false) String hxRequest,
    @RequestParam(defaultValue = "0") Integer page,
    @PathVariable String conversationId,
    Principal principal,
    Model model
  ) {
    PageRequest pageRequest = getPageRequest(page);
    UUID convId = UUID.fromString(conversationId);
    Page<SectionReadDto> sectionPage = queryFacade.getSectionPage(pageRequest, convId);
    ConversationData data = queryFacade.getConversationData(convId);
    model.addAttribute(ModelAttr.selectedData, data);
    model.addAttribute(ModelAttr.sectionPage, sectionPage);
    model.addAttribute(ModelAttr.requestGenerateDto, getEmptyRequest());
    model.addAttribute(ModelAttr.conversationId, convId);
    model.addAttribute(ModelAttr.isHxRequest, hxRequest);
    if (Objects.nonNull(hxRequest)) {
      return "conversation/window :: fragment";
    } else {
      UUID accountId = UUID.fromString(principal.getName());
      ConversationReadDto selected = queryFacade.getConversation(convId, accountId);
      conversationPage.list(0, principal, model);
      model.addAttribute(ModelAttr.selectedConversation, selected);
      return "conversation/window";
    }
  }

  @GetMapping("/{conversationId}/previous")
  @ResponseStatus(HttpStatus.OK)
  String windowPrevious(
    @RequestParam(defaultValue = "0") Integer page,
    @PathVariable String conversationId,
    Model model
  ) {
    PageRequest pageRequest = getPageRequest(page);
    UUID convId = UUID.fromString(conversationId);
    Page<SectionReadDto> sectionPage = queryFacade.getSectionPage(pageRequest, convId);
    model.addAttribute(ModelAttr.sectionPage, sectionPage);
    model.addAttribute(ModelAttr.conversationId, convId);
    return "conversation/window-content :: previous-fragment";
  }

  @GetMapping("/{conversationId}/next")
  @ResponseStatus(HttpStatus.OK)
  String windowNext(
    @RequestParam(defaultValue = "0") Integer page,
    @PathVariable String conversationId,
    Model model
  ) {
    PageRequest pageRequest = getPageRequest(page);
    UUID convId = UUID.fromString(conversationId);
    Page<SectionReadDto> sectionPage = queryFacade.getSectionPage(pageRequest, convId);
    model.addAttribute(ModelAttr.sectionPage, sectionPage);
    model.addAttribute(ModelAttr.conversationId, convId);
    return "conversation/window-content :: next-fragment";
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
    model.addAttribute(ModelAttr.sectionReadDto, readDto);
    return "conversation/window-content :: singular-fragment";
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
    model.addAttribute(ModelAttr.requestReadDto, readDto);
    model.addAttribute(ModelAttr.sectionId, sectionId);
    return "conversation/window-content :: request-fragment";
  }

  @PostMapping("/request/{requestId}")
  @ResponseStatus(HttpStatus.CREATED)
  String retry(
    @PathVariable String requestId,
    Model model
  ) {
    ResponseReadDto readDto = commandFacade.retry(UUID.fromString(requestId));
    model.addAttribute(ModelAttr.responseReadDto, readDto);
    model.addAttribute(ModelAttr.requestId, requestId);
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
    model.addAttribute(ModelAttr.requestReadDto, requestDto);
    model.addAttribute(ModelAttr.sectionId, sectionId);
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
    model.addAttribute(ModelAttr.responseReadDto, responseDto);
    model.addAttribute(ModelAttr.requestId, requestId);
    return "conversation/window-content :: response-fragment";
  }

  @DeleteMapping("/section/{sectionId}")
  String deleteSection(
    @PathVariable String sectionId,
    Principal principal
  ) {
    commandFacade.deleteSection(
      UUID.fromString(sectionId),
      UUID.fromString(principal.getName())
    );
    return "empty";
  }

  @DeleteMapping("/request/{requestId}")
  ResponseEntity<Void> deleteRequest(
    @PathVariable String requestId,
    Principal principal
  ) {
    commandFacade.deleteRequest(
      UUID.fromString(requestId),
      UUID.fromString(principal.getName())
    );
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/response/{responseId}")
  ResponseEntity<Void> deleteResponse(
    @PathVariable String responseId,
    Principal principal
  ) {
    commandFacade.deleteResponse(
      UUID.fromString(responseId),
      UUID.fromString(principal.getName())
    );
    return ResponseEntity.noContent().build();
  }

  private static PageRequest getPageRequest(Integer page) {
    return PageRequest.of(page, Constants.PAGE_SIZE, Sort.by("created").descending());
  }
}