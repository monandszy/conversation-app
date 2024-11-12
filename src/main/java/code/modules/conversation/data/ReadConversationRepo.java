package code.modules.conversation.data;

import static code.modules.conversation.ConversationQueryFacade.ConversationData;
import code.modules.conversation.data.jpa.ConversationJpaRepo;
import code.modules.conversation.data.jpa.RequestJpaRepo;
import code.modules.conversation.data.jpa.RequestNavigationProjection;
import code.modules.conversation.data.jpa.ResponseJpaRepo;
import code.modules.conversation.data.jpa.ResponseNavigationProjection;
import code.modules.conversation.data.jpa.SectionJpaRepo;
import code.modules.conversation.data.jpa.SectionNavigationProjection;
import code.modules.conversation.service.Conversation;
import code.modules.conversation.service.ReadConversationDao;
import code.modules.conversation.service.Request;
import code.modules.conversation.service.Response;
import code.modules.conversation.service.Section;
import code.modules.conversation.util.ConversationMapper;
import code.util.ReadRepositoryAdapter;
import jakarta.persistence.Tuple;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@ReadRepositoryAdapter
@AllArgsConstructor
public class ReadConversationRepo implements ReadConversationDao {

  private ResponseJpaRepo responseJpaRepo;
  private RequestJpaRepo requestJpaRepo;
  private ConversationJpaRepo conversationJpaRepo;
  private SectionJpaRepo sectionJpaRepo;
  private ConversationMapper mapper;

  @Override
  public Page<Section> getSectionPage(PageRequest pageRequest, Conversation conversation) {
    ConversationEntity entity = mapper.domainToEntity(conversation);
    Page<SectionNavigationProjection> page = sectionJpaRepo
      .findSectionWithSelectedRequestAndResponse(entity, pageRequest);
    return page.map(projection -> mapper.entityToDomain(projection));
  }

  @Override
  public Page<Conversation> getConversationPage(PageRequest pageRequest, UUID accountId) {
    Page<ConversationEntity> page = conversationJpaRepo.findByAccountId(accountId, pageRequest);
    return page.map(mapper::entityToDomain);
  }

  @Override
  public Request getRequestWithNavigation(UUID requestId, UUID sectionId) {
    RequestEntity entity = requestJpaRepo.findById(requestId).orElseThrow();
    requestJpaRepo.deselectOther(SectionEntity.builder().id(sectionId).build());
    entity.setSelected(true);
    RequestNavigationProjection projection = requestJpaRepo.findByRequest(entity);
    return mapper.entityToDomain(projection);
  }

  @Override
  public Request getRequest(UUID requestId) {
    RequestEntity entity = requestJpaRepo.findById(requestId).orElseThrow();
    return mapper.entityToDomain(entity);
  }

  @Override
  public Response getResponseWithNavigation(UUID responseId, UUID requestId) {
    ResponseEntity entity = responseJpaRepo.findById(responseId).orElseThrow();
    responseJpaRepo.deselectOther(RequestEntity.builder().id(requestId).build());
    entity.setSelected(true);
    ResponseNavigationProjection projection = responseJpaRepo.findByResponse(entity);
    return mapper.entityToDomain(projection);
  }

  @Override
  public Conversation getConversation(Conversation conversation) {
    ConversationEntity entity = conversationJpaRepo.findById(conversation.getId()).orElseThrow();
    return mapper.entityToDomain(entity);
  }

  @Override
  public ConversationData getConversationData(
    Conversation conversation
  ) {
    ConversationEntity entity = mapper.domainToEntity(conversation);
    Tuple tuple = sectionJpaRepo.countSectionsRequestsResponses(entity);
    ConversationData conversationData = new ConversationData();
    conversationData.setSectionCount(tuple.get("sectionCount", Long.class));
    conversationData.setRequestCount(tuple.get("requestCount", Long.class));
    conversationData.setResponseCount(tuple.get("responseCount", Long.class));
    return conversationData;
  }
}