package code.modules.conversation.data;

import code.modules.conversation.data.jpa.ConversationJpaRepo;
import code.modules.conversation.data.jpa.SectionJpaRepo;
import code.modules.conversation.service.Conversation;
import code.modules.conversation.service.ConversationDao;
import code.modules.conversation.service.Section;
import code.modules.conversation.util.ConversationMapper;
import code.util.RepositoryAdapter;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@RepositoryAdapter
@AllArgsConstructor
public class ConversationRepository implements ConversationDao {

  private ConversationJpaRepo conversationJpaRepo;
  private SectionJpaRepo sectionJpaRepo;
  private ConversationMapper mapper;

  @Override
  public Section create(Section section) {
    SectionEntity entity = mapper.domainToEntity(section);
    entity.setConversation(mapper.domainToEntity(section.getConversation()));
    SectionEntity saved = sectionJpaRepo.save(entity);
//    List<RequestEntity> responseEntities = section.getRequests().stream().map(request -> {
//      RequestEntity requestEntity = mapper.domainToEntity(request);
//      requestEntity.getResponses().stream().map(response -> {
//        return ResponseEntity
//      })
//      responseEntity.setRequest(saved);
//      return requestEntity;
//    }).toList();
//    responseEntities = responseJpaRepo.saveAll(responseEntities);
//    saved.setResponses(responseEntities);
    return mapper.entityToDomain(saved);
  }

  @Override
  public Conversation create(Conversation conversation) {
    ConversationEntity entity = mapper.domainToEntity(conversation);
    ConversationEntity saved = conversationJpaRepo.save(entity);
    return mapper.entityToDomain(saved);
  }

  @Override
  public Page<Conversation> getConversationPage(PageRequest pageRequest, UUID accountId) {
    Page<ConversationEntity> page = conversationJpaRepo.findByAccountId(accountId, pageRequest);
    return page.map(mapper::entityToDomain);
  }

  @Override
  public Page<Section> getSectionPage(PageRequest pageRequest, Conversation conversation) {
    ConversationEntity entity = mapper.domainToEntity(conversation);
    Page<SectionEntity> page = sectionJpaRepo.findByConversation(entity, pageRequest);
    return page.map(save -> mapper.entityToDomain(save));
  }
}