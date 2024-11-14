package code.modules.conversation;

import java.util.List;
import java.util.UUID;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface IConversationQueryFacade {

  Page<ConversationReadDto> getConversationPage(PageRequest pageRequest, UUID accountId);

  ConversationReadDto getConversation(UUID conversationId, UUID accountId);

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
    UUID nextId,
    UUID previousId
  ) {}

  @Data
  class ConversationData {
    private Long sectionCount;
    private Long requestCount;
    private Long responseCount;
    private UUID conversationId;
  }
}