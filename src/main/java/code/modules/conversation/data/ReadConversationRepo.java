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
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    long totalAmount = conversationJpaRepo.countByAccountId(accountId);
    int pageCount = (int) Math.floor(((double) totalAmount) / pageRequest.getPageSize());
    int pageNumber = pageCount - pageRequest.getPageNumber();
    int minPosition = pageNumber * pageRequest.getPageSize();
    List<ConversationEntity> page = conversationJpaRepo
      .findContentByAccountId(accountId, Constants.PAGE_SIZE, minPosition);
    List<Conversation> content = page.stream().map(mapper::entityToDomain).toList();
    return new PageImpl<>(content, pageRequest, totalAmount);
  }

  @Override
  public Page<Conversation> getConversationPageWithFilter(ConversationId conversationId, AccountId accountId) {
    Object[] pageData = (Object[]) conversationJpaRepo.getSelectedPosition(conversationId.value(), accountId.value())[0];
    Double selectedPosition = (Double) pageData[0];
    Long totalAmount = (Long) pageData[1];
    int pageCount = (int) Math.floor(((double) totalAmount) / Constants.PAGE_SIZE);
    int pageNumber = (int) Math.floor(selectedPosition / Constants.PAGE_SIZE);
    int minPosition = pageNumber * Constants.PAGE_SIZE;
    List<ConversationEntity> page = conversationJpaRepo
      .findContentByAccountId(accountId, Constants.PAGE_SIZE, minPosition);
    List<Conversation> content = page.stream().map(mapper::entityToDomain).toList();
    pageNumber = pageCount - pageNumber; // page number needs to be reversed, for the frontend logic.
    return new PageImpl<>(content, PageRequest.of(pageNumber, Constants.PAGE_SIZE), totalAmount);
  }

  @Override
  public Page<Section> getSectionPage(PageRequest pageRequest, ConversationId conversationId) {
    long totalAmount = sectionJpaRepo.countByConversationId(conversationId);
    int pageCount = (int) Math.floor(((double) totalAmount) / pageRequest.getPageSize());
    int pageNumber = pageCount - pageRequest.getPageNumber();
    int minPosition = pageNumber * pageRequest.getPageSize();
    List<Object[]> page = sectionJpaRepo
      .findProjectionPageByConversationId(conversationId, Constants.PAGE_SIZE, minPosition);
    List<Section> content = page.stream().map(mapper::sectionProjectionToDomain).toList();
    return new PageImpl<>(content, pageRequest, totalAmount);
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