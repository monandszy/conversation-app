package code.modules.conversation;

import code.modules.conversation.IConversationQueryFacade.ConversationReadDto;
import code.modules.conversation.IConversationQueryFacade.RequestReadDto;
import code.modules.conversation.IConversationQueryFacade.ResponseReadDto;
import code.modules.conversation.IConversationQueryFacade.SectionReadDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public interface IConversationCommandFacade {

  ConversationReadDto begin(UUID accountId);

  SectionReadDto generate(@Valid GenerateDto generateDto, UUID conversationId);

  RequestReadDto regenerate(@Valid GenerateDto generateDto, UUID sectionId);

  ResponseReadDto retry(UUID requestId);

  void deleteConversation(UUID conversationId);

  void deleteSection(UUID sectionId);

  void deleteRequest(UUID requestId);

  void deleteResponse(UUID responseId);

  record GenerateDto(
    @NotBlank
    String text
  ) {
    public static GenerateDto getEmptyRequest() {
      return new GenerateDto(null);
    }
  }
}