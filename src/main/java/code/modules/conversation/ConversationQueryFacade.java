package code.modules.conversation;

import code.modules.conversation.service.Conversation;
import code.modules.conversation.service.ReadConversationDao;
import code.modules.conversation.service.Request;
import code.modules.conversation.service.Response;
import code.modules.conversation.service.Section;
import code.modules.conversation.util.ConversationMapper;
import code.util.Facade;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.With;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Facade
@Slf4j
@AllArgsConstructor
public class ConversationQueryFacade {

  private ReadConversationDao readDao;
  private ConversationMapper mapper;

  public Page<ConversationReadDto> getConversationPage(PageRequest pageRequest, UUID accountId) {
    Page<ConversationReadDto> page = readDao.getConversationPage(pageRequest, accountId)
      .map(e -> mapper.domainToReadDto(e));
    log.info("Conversation Page: {}", page);
    return page;
  }

  public Page<SectionReadDto> getSectionPage(PageRequest pageRequest, UUID conversationId) {
    Conversation conversation = Conversation.builder().id(conversationId).build();
    Page<Section> sectionPage = readDao.getSectionPage(pageRequest, conversation);
    Page<SectionReadDto> page = sectionPage.map(e -> mapper.domainToReadDto(e));
    log.info("Conversation Item Page: {}", page);
    return page;
  }


  public RequestReadDto getRequest(UUID requestId, UUID sectionId) {
    Request request = readDao.getRequestWithNavigation(requestId, sectionId);
    return mapper.domainToReadDto(request);
  }

  public ResponseReadDto getResponse(UUID responseId, UUID requestId) {
    Response response = readDao.getResponseWithNavigation(responseId, requestId);
    return mapper.domainToReadDto(response);
  }

  @With
  public record ConversationReadDto(
    UUID id
  ) {}

  @With
  public record SectionReadDto(
    UUID id,
    List<RequestReadDto> requests
  ) {}

  @With
  public record RequestReadDto(
    UUID id,
    String text,
    List<ResponseReadDto> responses,
    NavigationDto navigation
  ) {}

  @With
  public record ResponseReadDto(
    UUID id,
    String text,
    NavigationDto navigation
  ) {}

  @With
  public record NavigationDto(
    UUID nextId,
    UUID previousId
  ) {}
}