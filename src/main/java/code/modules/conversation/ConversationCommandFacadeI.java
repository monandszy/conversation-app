package code.modules.conversation;

import code.modules.conversation.service.ConversationCommandFacade;
import code.modules.conversation.service.ConversationQueryFacade;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.With;

public interface ConversationCommandFacadeI {

  ConversationQueryFacade.ConversationReadDto begin(@Valid ConversationCommandFacade.ConversationBeginDto conversationDto);
  ConversationQueryFacade.SectionReadDto generate(@Valid ConversationCommandFacade.RequestGenerateDto generateDto, UUID conversationId);
  ConversationQueryFacade.RequestReadDto regenerate(@Valid ConversationCommandFacade.RequestGenerateDto generateDto, UUID sectionId);
  ConversationQueryFacade.ResponseReadDto retry(UUID requestId);
  void deleteConversation(UUID conversationId, UUID accountId);
  void deleteSection(UUID sectionId, UUID accountId);
  void deleteRequest(UUID requestId, UUID accountId);
  void deleteResponse(UUID responseId, UUID accountId);

  record ConversationBeginDto(
    @NotNull
    UUID accountId
  ) {}

  @With
  record RequestGenerateDto(
    @NotBlank
    String text
  ) {}
}