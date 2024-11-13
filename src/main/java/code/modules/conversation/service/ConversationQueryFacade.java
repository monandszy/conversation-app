package code.modules.conversation.service;

import code.modules.conversation.ConversationQueryFacadeI;
import code.modules.conversation.service.domain.Conversation;
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
public class ConversationQueryFacade implements ConversationQueryFacadeI {

  private ReadConversationDao readDao;
  private ConversationMapper mapper;

  public Page<ConversationReadDto> getConversationPage(PageRequest pageRequest, UUID accountId) {
    Page<ConversationReadDto> page = readDao.getConversationPage(pageRequest, accountId)
      .map(e -> mapper.domainToReadDto(e));
    log.info("Conversation Page: {}", page);
    return page;
  }

  public ConversationReadDto getConversation(UUID conversationId, UUID accountId) {
    Conversation conversation = Conversation.builder().id(conversationId)
      .accountId(accountId).build();
    conversation = readDao.getConversation(conversation);
    return mapper.entityToDomain(conversation);
  }

  public Page<SectionReadDto> getSectionPage(PageRequest pageRequest, UUID conversationId) {
    Conversation conversation = Conversation.builder().id(conversationId).build();
    Page<Section> sectionPage = readDao.getSectionPage(pageRequest, conversation);
    Page<SectionReadDto> page = sectionPage.map(e -> mapper.domainToReadDto(e));
    log.info("Conversation Item Page: {}", page);
    return page;
  }

  public RequestReadDto getRequest(UUID requestId, UUID sectionId) {
    Request request = Request.builder().id(requestId).build();
    Section section = Section.builder().id(sectionId).build();
    request = readDao.getRequestWithNavigation(request, section);
    return mapper.domainToReadDto(request);
  }

  public ResponseReadDto getResponse(UUID responseId, UUID requestId) {
    Response response = Response.builder().id(responseId).build();
    Request request = Request.builder().id(requestId).build();
    response = readDao.getResponseWithNavigation(response, request);
    return mapper.domainToReadDto(response);
  }

  public ConversationData getConversationData(UUID conversationId) {
    Conversation conversation = Conversation.builder().id(conversationId).build();
    ConversationData conversationData = readDao.getConversationData(conversation);
    conversationData.setConversationId(conversationId);
    return conversationData;
  }
}