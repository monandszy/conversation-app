package code.modules.conversation.data;

import code.modules.conversation.data.entity.ConversationEntity;
import code.modules.conversation.data.entity.RequestEntity;
import code.modules.conversation.data.entity.ResponseEntity;
import code.modules.conversation.data.entity.SectionEntity;
import code.modules.conversation.data.jpa.ConversationJpaRepo;
import code.modules.conversation.data.jpa.RequestJpaRepo;
import code.modules.conversation.data.jpa.ResponseJpaRepo;
import code.modules.conversation.data.jpa.SectionJpaRepo;
import code.modules.conversation.data.jpa.projection.SectionWindow;
import code.modules.conversation.service.CommandConversationDao;
import code.modules.conversation.service.domain.Conversation;
import code.modules.conversation.service.domain.Request;
import code.modules.conversation.service.domain.Response;
import code.modules.conversation.service.domain.Section;
import code.modules.conversation.util.ConversationMapper;
import code.util.CommandRepositoryAdapter;
import java.util.UUID;
import lombok.AllArgsConstructor;

@CommandRepositoryAdapter
@AllArgsConstructor
public class CommandConversationRepo implements CommandConversationDao {

  private final ResponseJpaRepo responseJpaRepo;
  private final RequestJpaRepo requestJpaRepo;
  private ConversationJpaRepo conversationJpaRepo;
  private SectionJpaRepo sectionJpaRepo;
  private ConversationMapper mapper;

  @Override
  public Conversation create(Conversation conversation) {
    ConversationEntity entity = mapper.domainToEntity(conversation);
    ConversationEntity saved = conversationJpaRepo.save(entity);
    return mapper.entityToDomain(saved);
  }

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
    SectionWindow projection = sectionJpaRepo.findProjectionBySection(saved);
    return mapper.entityToDomain(projection);
  }

  @Override
  public Request create(Request request, Response response) {
    RequestEntity requestEntity = mapper.domainToEntity(request);
    SectionEntity section = requestEntity.getSection();
    ResponseEntity responseEntity = mapper.domainToEntity(response);
    responseEntity.setRequest(requestEntity);
    requestEntity.getResponses().add(responseEntity);
    RequestEntity saved = requestJpaRepo.save(requestEntity);
    requestJpaRepo.deselectAndSelect(section, saved);
    Object[] projection = requestJpaRepo.findProjectionByRequest(saved.getId());
    return mapper.requestProjectionToDomain((Object[]) projection[0]);
  }

  @Override
  public Response create(Response response) {
    ResponseEntity entity = mapper.domainToEntity(response);
    RequestEntity request = entity.getRequest();
    ResponseEntity saved = responseJpaRepo.save(entity);
    responseJpaRepo.deselectAndSelect(request, saved);
    Object[] projection = responseJpaRepo.findProjectionByResponse(saved.getId());
    return mapper.responseProjectionToDomain((Object[]) projection[0]);
  }

  @Override
  public void deleteConversation(Conversation conversation, UUID accountId) {
    int rowsDeleted = conversationJpaRepo
      .deleteByIdAndAccountId(conversation.getId(), accountId);
    if (rowsDeleted == 0) {
      throw new IllegalArgumentException(
        "Conversation does not exist or is not associated with the given account.");
    }
  }

  @Override
  public void deleteSection(Section section, UUID accountId) {
    int rowsDeleted = sectionJpaRepo
      .deleteByIdAndConversationAccountId(section.getId(), accountId);
    if (rowsDeleted == 0) {
      throw new IllegalArgumentException(
        "Section does not exist or is not associated with the given account.");
    }
  }

  @Override
  public void deleteRequest(Request request, UUID accountId) {
    int rowsDeleted = requestJpaRepo
      .deleteByIdAndSectionConversationAccountId(request.getId(), accountId);
    if (rowsDeleted == 0) {
      throw new IllegalArgumentException(
        "Request does not exist or is not associated with the given account.");
    }
  }

  @Override
  public void deleteResponse(Response response, UUID accountId) {
    int rowsDeleted = responseJpaRepo
      .deleteByIdAndRequestSectionConversationAccountId(response.getId(), accountId);
    if (rowsDeleted == 0) {
      throw new IllegalArgumentException(
        "Response does not exist or is not associated with the given account.");
    }
  }
}