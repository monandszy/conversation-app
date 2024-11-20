package code.modules.conversation;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface IConversationQueryFacade {

  Page<ConversationReadDto> getConversationPage(PageRequest pageRequest, UUID accountId);

  Page<ConversationReadDto> getFilteredConversationPage(UUID conversationId, UUID accountId);

  Page<SectionReadDto> getSectionPage(PageRequest pageRequest, UUID conversationId);

  RequestReadDto getRequest(UUID requestId, UUID sectionId);

  ResponseReadDto getResponse(UUID responseId, UUID requestId);

  ConversationData getConversationData(UUID conversationId);

  record ConversationReadDto(
    UUID id
  ) {}

  record SectionReadDto(
    UUID id,
    List<RequestReadDto> requests
  ) {}

  record RequestReadDto(
    UUID id,
    String text,
    List<ResponseReadDto> responses,
    NavigationDto navigation
  ) {}

  record ResponseReadDto(
    UUID id,
    String text,
    NavigationDto navigation
  ) {}

  record NavigationDto(
    Long count,
    Long position,
    UUID nextId,
    UUID previousId
  ) {
    public boolean isNotSingle() {
      return count != 1;
    }
  }

  record ConversationData(
    Long sectionCount,
    Long requestCount,
    Long responseCount,
    UUID conversationId
  ) {
  }
}