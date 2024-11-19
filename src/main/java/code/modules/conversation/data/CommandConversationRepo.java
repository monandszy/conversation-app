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
  public void deleteConversation(Conversation conversation) {
    conversationJpaRepo.deleteById(conversation.getId());
  }

  @Override
  public void deleteSection(Section section) {
    sectionJpaRepo.deleteById(section.getId());
  }

  @Override
  public void deleteRequest(Request request) {
    requestJpaRepo.deleteById(request.getId());
  }

  @Override
  public void deleteResponse(Response response) {
    responseJpaRepo.deleteById(response.getId());
  }
}