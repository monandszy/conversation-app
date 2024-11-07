package code.modules.conversation.data;

import code.modules.conversation.data.jpa.ConversationJpaRepo;
import code.modules.conversation.data.jpa.RequestJpaRepo;
import code.modules.conversation.data.jpa.ResponseJpaRepo;
import code.modules.conversation.service.Conversation;
import code.modules.conversation.service.ConversationDao;
import code.modules.conversation.service.Request;
import code.modules.conversation.util.ConversationMapper;
import code.util.RepositoryAdapter;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@RepositoryAdapter
@AllArgsConstructor
public class ConversationRepository implements ConversationDao {

  private ConversationJpaRepo conversationJpaRepo;
  private RequestJpaRepo requestJpaRepo;
  private ResponseJpaRepo responseJpaRepo;
  private ConversationMapper mapper;

  @Override
  public Request create(Request request) {
    RequestEntity entity = mapper.domainToEntity(request);
    entity.setConversation(mapper.domainToEntity(request.getConversation()));
    entity.setCreated(OffsetDateTime.now());
    RequestEntity saved = requestJpaRepo.save(entity);
    List<ResponseEntity> responseEntities = request.getResponses().stream().map(response -> {
      ResponseEntity responseEntity = mapper.domainToEntity(response);
      responseEntity.setRequest(saved);
      return responseEntity;
    }).toList();
    responseEntities = responseJpaRepo.saveAll(responseEntities);
    saved.setResponses(responseEntities);
    return mapper.entityToDomain(saved);
  }

  @Override
  public Conversation create(Conversation conversation) {
    ConversationEntity entity = mapper.domainToEntity(conversation);
    entity.setCreated(OffsetDateTime.now());
    ConversationEntity saved = conversationJpaRepo.save(entity);
    return mapper.entityToDomain(saved);
  }

  @Override
  public Page<Conversation> getConversationPage(PageRequest pageRequest, UUID accountId) {
    Page<ConversationEntity> page = conversationJpaRepo.findByAccountId(accountId, pageRequest);
    return page.map(mapper::entityToDomain);
  }

  @Override
  public Page<Request> getRequestPage(PageRequest pageRequest, Conversation conversation) {
    ConversationEntity entity = mapper.domainToEntity(conversation);
    Page<RequestEntity> page = requestJpaRepo.findByConversation(entity, pageRequest);
    return page.map(save -> mapper.entityToDomain(save));
  }
}