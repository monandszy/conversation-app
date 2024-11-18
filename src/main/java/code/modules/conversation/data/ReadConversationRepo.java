package code.modules.conversation.data;

import static code.modules.conversation.IConversationQueryFacade.ConversationData;

import code.modules.conversation.data.entity.ConversationEntity;
import code.modules.conversation.data.entity.RequestEntity;
import code.modules.conversation.data.entity.ResponseEntity;
import code.modules.conversation.data.jpa.ConversationJpaRepo;
import code.modules.conversation.data.jpa.RequestJpaRepo;
import code.modules.conversation.data.jpa.ResponseJpaRepo;
import code.modules.conversation.data.jpa.SectionJpaRepo;
import code.modules.conversation.data.jpa.projection.RequestWindow;
import code.modules.conversation.data.jpa.projection.ResponseWindow;
import code.modules.conversation.data.jpa.projection.SectionWindow;
import code.modules.conversation.service.ReadConversationDao;
import code.modules.conversation.service.domain.Conversation;
import code.modules.conversation.service.domain.Request;
import code.modules.conversation.service.domain.Response;
import code.modules.conversation.service.domain.Section;
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
  public Page<Conversation> getConversationPage(PageRequest pageRequest, UUID accountId) {
    Page<ConversationEntity> page = conversationJpaRepo.findByAccountId(accountId, pageRequest);
    return page.map(mapper::entityToDomain);
  }

  @Override
  public Page<Section> getSectionPage(PageRequest pageRequest, Conversation conversation) {
    ConversationEntity entity = mapper.domainToEntity(conversation);
    Page<SectionWindow> page = sectionJpaRepo.findProjectionPageByConversation(entity, pageRequest);
    return page.map(window -> mapper.entityToDomain(window));
  }

  @Override
  public Request getRequestWithNavigation(Request request, Section section) {
    RequestEntity entity = mapper.domainToEntity(request);
    requestJpaRepo.deselectAndSelect(
      mapper.domainToEntity(section),
      entity
    );
    RequestWindow window = requestJpaRepo.findProjectionByRequest(entity);
    return mapper.entityToDomain(window);
  }

  @Override
  public Response getResponseWithNavigation(Response response, Request request) {
    ResponseEntity entity = mapper.domainToEntity(response);
    responseJpaRepo.deselectAndSelect(
      mapper.domainToEntity(request),
      entity
    );
    ResponseWindow window = responseJpaRepo.findProjectionByResponse(entity);
    return mapper.entityToDomain(window);
  }

  @Override
  public Request getRequest(Request request) {
    RequestEntity entity = requestJpaRepo
      .findById(request.getId()).orElseThrow();
    return mapper.entityToDomain(entity);
  }

  @Override
  public Conversation getConversation(Conversation conversation) {
    ConversationEntity entity = conversationJpaRepo
      .findById(conversation.getId()).orElseThrow();
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