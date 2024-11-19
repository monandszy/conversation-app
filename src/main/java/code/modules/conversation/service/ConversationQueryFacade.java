package code.modules.conversation.service;

import code.modules.conversation.IConversationQueryFacade;
import code.modules.conversation.service.domain.AccountId;
import code.modules.conversation.service.domain.Conversation.ConversationId;
import code.modules.conversation.service.domain.Request;
import code.modules.conversation.service.domain.Response;
import code.modules.conversation.service.domain.Section;
import code.modules.conversation.util.ConversationMapper;
import code.util.Facade;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Facade
@Slf4j
@AllArgsConstructor
public class ConversationQueryFacade implements IConversationQueryFacade {

  private ReadConversationDao readDao;
  private ConversationMapper mapper;

  @Override
  public Page<ConversationReadDto> getConversationPage(PageRequest pageRequest, UUID accountId) {
    Page<ConversationReadDto> page = readDao.getConversationPage(
      pageRequest, new AccountId(accountId)
    ).map(e -> mapper.domainToReadDto(e));
    log.info("Conversation Page: {}", page);
    return page;
  }

  @Override
  public Page<SectionReadDto> getSectionPage(PageRequest pageRequest, UUID conversationId) {
    Page<Section> sectionPage = readDao.getSectionPage(
      pageRequest, new ConversationId(conversationId)
    );
    Page<SectionReadDto> page = sectionPage.map(e -> mapper.domainToReadDto(e));
    log.info("Conversation Item Page: {}", page);
    return page;
  }

  @Override
  public RequestReadDto getRequest(UUID requestId, UUID sectionId) {
    Request request = readDao.getRequestWithNavigation(
      new Request.RequestId(requestId),
      new Section.SectionId(sectionId)
    );
    return mapper.domainToReadDto(request);
  }

  @Override
  public ResponseReadDto getResponse(UUID responseId, UUID requestId) {
    Response response = readDao.getResponseWithNavigation(
      new Response.ResponseId(responseId),
      new Request.RequestId(requestId)
    );
    return mapper.domainToReadDto(response);
  }

  @Override
  public ConversationData getConversationData(UUID conversationId) {
    return readDao.getConversationData(new ConversationId(conversationId));
  }
}