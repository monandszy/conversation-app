package code.modules.conversation.data;

import code.modules.conversation.data.entity.ConversationEntity;
import code.modules.conversation.data.entity.RequestEntity;
import code.modules.conversation.data.entity.ResponseEntity;
import code.modules.conversation.data.entity.SectionEntity;
import code.modules.conversation.data.jpa.ConversationJpaRepo;
import code.modules.conversation.data.jpa.RequestJpaRepo;
import code.modules.conversation.data.jpa.ResponseJpaRepo;
import code.modules.conversation.data.jpa.SectionJpaRepo;
import code.modules.conversation.service.CommandConversationDao;
import code.modules.conversation.service.domain.Conversation;
import code.modules.conversation.service.domain.Conversation.ConversationId;
import code.modules.conversation.service.domain.Request;
import code.modules.conversation.service.domain.Response;
import code.modules.conversation.service.domain.Section;
import code.modules.conversation.util.ConversationMapper;
import code.util.CommandRepositoryAdapter;
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
    ResponseEntity responseEntity = mapper.domainToEntity(response);;
    SectionEntity savedSection = sectionJpaRepo.save(sectionEntity);
    requestEntity.setSection(savedSection);
    RequestEntity saveRequest = requestJpaRepo.save(requestEntity);
    responseEntity.setRequest(saveRequest);
    responseJpaRepo.save(responseEntity);
    Object[] projection = sectionJpaRepo.findProjectionBySectionId(savedSection.getId());
    return mapper.sectionProjectionToDomain((Object[]) projection[0]);
  }

  @Override
  public Request create(Request request, Response response) {
    RequestEntity requestEntity = mapper.domainToEntity(request);
    SectionEntity section = requestEntity.getSection();
    ResponseEntity responseEntity = mapper.domainToEntity(response);
    RequestEntity saveRequest = requestJpaRepo.save(requestEntity);
    responseEntity.setRequest(saveRequest);
    responseJpaRepo.save(responseEntity);
    requestJpaRepo.deselectAndSelect(section.getId(), saveRequest.getId());
    Object[] projection = requestJpaRepo.findProjectionByRequest(saveRequest.getId().value());
    return mapper.requestProjectionToDomain((Object[]) projection[0]);
  }

  @Override
  public Response create(Response response) {
    ResponseEntity entity = mapper.domainToEntity(response);
    RequestEntity request = entity.getRequest();
    ResponseEntity saved = responseJpaRepo.save(entity);
    responseJpaRepo.deselectAndSelect(request.getId(), saved.getId());
    Object[] projection = responseJpaRepo.findProjectionByResponse(saved.getId().value());
    return mapper.responseProjectionToDomain((Object[]) projection[0]);
  }

  @Override
  public void deleteConversation(ConversationId conversationId) {
    conversationJpaRepo.deleteById(conversationId);
  }

  @Override
  public void deleteSection(Section.SectionId sectionId) {
    sectionJpaRepo.deleteById(sectionId);
  }

  @Override
  public void deleteRequest(Request.RequestId requestId) {
    requestJpaRepo.deleteById(requestId);
  }

  @Override
  public void deleteResponse(Response.ResponseId responseId) {
    responseJpaRepo.deleteById(responseId);
  }
}