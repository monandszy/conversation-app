package code.frontend.conversation;

import static code.modules.conversation.IConversationQueryFacade.ConversationReadDto;
import static code.modules.conversation.IConversationQueryFacade.ResponseReadDto;

import code.modules.conversation.IConversationCommandFacade;
import code.modules.conversation.IConversationCommandFacade.GenerateDto;
import code.modules.conversation.IConversationQueryFacade.RequestReadDto;
import code.modules.conversation.IConversationQueryFacade.SectionReadDto;
import code.util.ModelAttr;
import java.security.Principal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;


@Controller
@AllArgsConstructor
@RequestMapping("/conversation")
@Slf4j
public class CreateAndDeleteController {

  private IConversationCommandFacade commandFacade;

  @PostMapping("")
  String beginConversation(
    @ModelAttribute GenerateDto generateDto,
    Principal principal,
    Model model
  ) {
    ConversationReadDto readDto = commandFacade.begin(UUID.fromString(principal.getName()));
    commandFacade.generate(generateDto, readDto.id());
    model.addAttribute(ModelAttr.conversationReadDto, readDto);
    return "conversation/sidebar :: singular-fragment";
  }

  @PreAuthorize("@ownershipValidator.validateConversation(principal, #conversationId)")
  @DeleteMapping("/{conversationId}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  String deleteConversation(
    @PathVariable UUID conversationId
  ) {
    commandFacade.deleteConversation(conversationId);
    return "empty";
  }

  @PreAuthorize("@ownershipValidator.validateSection(principal, #sectionId)")
  @DeleteMapping("/section/{sectionId}")
  String deleteSection(
    @PathVariable UUID sectionId
  ) {
    commandFacade.deleteSection(sectionId);
    return "empty";
  }

  @PreAuthorize("@ownershipValidator.validateRequest(principal, #requestId)")
  @DeleteMapping("/request/{requestId}")
  ResponseEntity<Void> deleteRequest(
    @PathVariable UUID requestId
  ) {
    commandFacade.deleteRequest(requestId);
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("@ownershipValidator.validateResponse(principal, #responseId)")
  @DeleteMapping("/response/{responseId}")
  ResponseEntity<Void> deleteResponse(
    @PathVariable UUID responseId
  ) {
    commandFacade.deleteResponse(responseId);
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("@ownershipValidator.validateConversation(principal, #conversationId)")
  @PostMapping("/{conversationId}")
  @ResponseStatus(HttpStatus.CREATED)
  String generate(
    @ModelAttribute GenerateDto generateDto,
    @PathVariable UUID conversationId,
    Model model
  ) {
    SectionReadDto readDto = commandFacade.generate(generateDto, conversationId);
    model.addAttribute(ModelAttr.sectionReadDto, readDto);
    return "conversation/window-content :: singular-fragment";
  }

  @PreAuthorize("@ownershipValidator.validateSection(principal, #sectionId)")
  @PostMapping("/section/{sectionId}")
  @ResponseStatus(HttpStatus.CREATED)
  String regenerate(
    @ModelAttribute GenerateDto generateDto,
    @PathVariable UUID sectionId,
    Model model
  ) {
    RequestReadDto readDto = commandFacade.regenerate(generateDto, sectionId);
    model.addAttribute(ModelAttr.requestReadDto, readDto);
    model.addAttribute(ModelAttr.sectionId, sectionId);
    return "conversation/window-content :: request-fragment";
  }

  @PreAuthorize("@ownershipValidator.validateRequest(principal, #requestId)")
  @PostMapping("/request/{requestId}")
  @ResponseStatus(HttpStatus.CREATED)
  String retry(
    @PathVariable UUID requestId,
    Model model
  ) {
    ResponseReadDto readDto = commandFacade.retry(requestId);
    model.addAttribute(ModelAttr.responseReadDto, readDto);
    model.addAttribute(ModelAttr.requestId, requestId);
    return "conversation/window-content :: response-fragment";
  }
}