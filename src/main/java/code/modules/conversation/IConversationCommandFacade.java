package code.modules.conversation;

import code.modules.conversation.IConversationQueryFacade.ConversationReadDto;
import code.modules.conversation.IConversationQueryFacade.RequestReadDto;
import code.modules.conversation.IConversationQueryFacade.ResponseReadDto;
import code.modules.conversation.IConversationQueryFacade.SectionReadDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.With;

public interface IConversationCommandFacade {

  ConversationReadDto begin(@Valid ConversationBeginDto conversationDto);

  SectionReadDto generate(@Valid RequestGenerateDto generateDto, UUID conversationId);

  RequestReadDto regenerate(@Valid RequestGenerateDto generateDto, UUID sectionId);

  ResponseReadDto retry(UUID requestId);

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