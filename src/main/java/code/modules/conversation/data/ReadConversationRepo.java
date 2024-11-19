package code.modules.conversation.data;

import static code.modules.conversation.IConversationQueryFacade.ConversationData;
import static code.modules.conversation.service.domain.Conversation.ConversationId;

import code.configuration.Constants;
import code.modules.conversation.data.entity.ConversationEntity;
import code.modules.conversation.data.entity.RequestEntity;
import code.modules.conversation.data.jpa.ConversationJpaRepo;
import code.modules.conversation.data.jpa.RequestJpaRepo;
import code.modules.conversation.data.jpa.ResponseJpaRepo;
import code.modules.conversation.data.jpa.SectionJpaRepo;
import code.modules.conversation.service.ReadConversationDao;
import code.modules.conversation.service.domain.AccountId;
import code.modules.conversation.service.domain.Conversation;
import code.modules.conversation.service.domain.Request;
import code.modules.conversation.service.domain.Response;
import code.modules.conversation.service.domain.Section;
import code.modules.conversation.util.ConversationMapper;
import code.util.ReadRepositoryAdapter;
import jakarta.persistence.Tuple;
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
  public Page<Conversation> getConversationPage(PageRequest pageRequest, AccountId accountId) {
    Page<ConversationEntity> page = conversationJpaRepo.findByAccountId(accountId, pageRequest);
    return page.map(mapper::entityToDomain);
  }

  @Override
  public Page<Conversation> getConversationPageWithFilter(ConversationId conversationId, AccountId accountId) {
    Object[] projection = conversationJpaRepo.findByAccountIdWithFiler(
      accountId.value(), conversationId.value(), Constants.PAGE_SIZE);
    return mapper.conversationProjectionToDomain((Object[]) projection[0]);
  }

  @Override
  public Page<Section> getSectionPage(PageRequest pageRequest, ConversationId conversationId) {
    Page<Object[]> page = sectionJpaRepo
      .findProjectionPageByConversationId(conversationId, pageRequest);
    return page.map(projection -> mapper.projectionToDomain(projection));
  }

  @Override
  public Request getRequestWithNavigation(Request.RequestId requestId, Section.SectionId sectionId) {
    requestJpaRepo.deselectAndSelect(
      sectionId,
      requestId
    );
    Object[] projection = requestJpaRepo.findProjectionByRequest(requestId.value());
    return mapper.requestProjectionToDomain((Object[]) projection[0]);
  }

  @Override
  public Response getResponseWithNavigation(Response.ResponseId responseId, Request.RequestId requestId) {
    responseJpaRepo.deselectAndSelect(
      requestId,
      responseId
    );
    Object[] projection = responseJpaRepo.findProjectionByResponse(responseId.value());
    return mapper.responseProjectionToDomain((Object[]) projection[0]);
  }

  @Override
  public Request getRequest(Request.RequestId requestId) {
    RequestEntity entity = requestJpaRepo
      .findById(requestId).orElseThrow();
    return mapper.entityToDomain(entity);
  }

  @Override
  public ConversationData getConversationData(
    ConversationId conversationId
  ) {
    Tuple tuple = sectionJpaRepo.countSectionsRequestsResponses(conversationId);
    return new ConversationData(
      tuple.get("sectionCount", Long.class),
      tuple.get("requestCount", Long.class),
      tuple.get("responseCount", Long.class),
      conversationId.value()
    );
  }
}