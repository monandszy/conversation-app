package code.modules.conversation.data;

import code.modules.conversation.data.jpa.ConversationJpaRepo;
import code.modules.conversation.data.jpa.RequestJpaRepo;
import code.modules.conversation.data.jpa.RequestNavigationProjection;
import code.modules.conversation.data.jpa.ResponseJpaRepo;
import code.modules.conversation.data.jpa.ResponseNavigationProjection;
import code.modules.conversation.data.jpa.SectionJpaRepo;
import code.modules.conversation.data.jpa.SectionNavigationProjection;
import code.modules.conversation.service.CommandConversationDao;
import code.modules.conversation.service.Conversation;
import code.modules.conversation.service.Request;
import code.modules.conversation.service.Response;
import code.modules.conversation.service.Section;
import code.modules.conversation.util.ConversationMapper;
import code.util.CommandRepositoryAdapter;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@CommandRepositoryAdapter
@AllArgsConstructor
public class CommandConversationRepo implements CommandConversationDao {

  private final ResponseJpaRepo responseJpaRepo;
  private final RequestJpaRepo requestJpaRepo;
  private ConversationJpaRepo conversationJpaRepo;
  private SectionJpaRepo sectionJpaRepo;
  private ConversationMapper mapper;

  @Override
  public Section create(Section section, Request request, Response response) {
    SectionEntity sectionEntity = mapper.domainToEntity(section);
    RequestEntity requestEntity = mapper.domainToEntity(request);
    ResponseEntity responseEntity = mapper.domainToEntity(response);
    requestEntity.setSection(sectionEntity);
    sectionEntity.getRequests().add(requestEntity);
    responseEntity.setRequest(requestEntity);
    requestEntity.getResponses().add(responseEntity);
    SectionEntity saved = sectionJpaRepo.save(sectionEntity);
    SectionNavigationProjection projection = sectionJpaRepo.findBySection(saved);
    return mapper.entityToDomain(projection);
  }

  @Override
  public Request create(Request request, Response response) {
    RequestEntity requestEntity = mapper.domainToEntity(request);
    requestJpaRepo.deselectOther(requestEntity.getSection());
    ResponseEntity responseEntity = mapper.domainToEntity(response);
    responseEntity.setRequest(requestEntity);
    requestEntity.getResponses().add(responseEntity);
    RequestEntity saved = requestJpaRepo.save(requestEntity);
    RequestNavigationProjection projection = requestJpaRepo.findByRequest(saved);
    return mapper.entityToDomain(projection);
  }

  @Override
  public Response create(Response response) {
    ResponseEntity entity = mapper.domainToEntity(response);
    responseJpaRepo.deselectOther(entity.getRequest());
    ResponseEntity saved = responseJpaRepo.save(entity);
    ResponseNavigationProjection projection = responseJpaRepo.findByResponse(saved);
    return mapper.entityToDomain(projection);
  }

  @Override
  public void deleteConversation(UUID conversationId, UUID accountId) {
    int rowsDeleted = conversationJpaRepo.deleteByIdAndAccountId(conversationId, accountId);
    if (rowsDeleted == 0) {
      // Handle case where no deletion occurred, meaning no match was found
      throw new IllegalArgumentException("Conversation does not exist or is not associated with the given account.");
    }
  }

  @Override
  public void deleteSection(UUID sectionId, UUID accountId) {
    int rowsDeleted = sectionJpaRepo.deleteByIdAndConversationAccountId(sectionId, accountId);
    if (rowsDeleted == 0) {
      throw new IllegalArgumentException("Section does not exist or is not associated with the given account.");
    }
  }

  @Override
  public void deleteRequest(UUID requestId, UUID accountId) {
    int rowsDeleted = requestJpaRepo
      .deleteByIdAndSectionConversationAccountId(requestId, accountId);
    if (rowsDeleted == 0) {
      throw new IllegalArgumentException("Request does not exist or is not associated with the given account.");
    }
  }

  @Override
  public void deleteResponse(UUID responseId, UUID accountId) {
    int rowsDeleted = responseJpaRepo
      .deleteByIdAndRequestSectionConversationAccountId(responseId, accountId);
    if (rowsDeleted == 0) {
      throw new IllegalArgumentException("Response does not exist or is not associated with the given account.");
    }
  }

  @Override
  @Transactional
  public Conversation create(Conversation conversation) {
    ConversationEntity entity = mapper.domainToEntity(conversation);
    ConversationEntity saved = conversationJpaRepo.save(entity);
    return mapper.entityToDomain(saved);
  }


}