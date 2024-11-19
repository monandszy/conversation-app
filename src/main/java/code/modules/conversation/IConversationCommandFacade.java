package code.modules.conversation;

import code.modules.conversation.IConversationQueryFacade.ConversationReadDto;
import code.modules.conversation.IConversationQueryFacade.RequestReadDto;
import code.modules.conversation.IConversationQueryFacade.ResponseReadDto;
import code.modules.conversation.IConversationQueryFacade.SectionReadDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.With;

public interface IConversationCommandFacade {

  ConversationReadDto begin(UUID accountId);

  SectionReadDto generate(@Valid RequestGenerateDto generateDto, UUID conversationId);

  RequestReadDto regenerate(@Valid RequestGenerateDto generateDto, UUID sectionId);

  ResponseReadDto retry(UUID requestId);

  void deleteConversation(UUID conversationId);

  void deleteSection(UUID sectionId);

  void deleteRequest(UUID requestId);

  void deleteResponse(UUID responseId);

  @With
  record RequestGenerateDto(
    @NotBlank
    String text
  ) {}
}