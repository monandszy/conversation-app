package code.modules.conversation.data;

import code.modules.conversation.data.jpa.ConversationJpaRepo;
import code.modules.conversation.data.jpa.ResponseJpaRepo;
import code.modules.conversation.data.jpa.SectionJpaRepo;
import code.modules.conversation.data.jpa.SectionNavigationProjection;
import code.modules.conversation.service.Conversation;
import code.modules.conversation.service.ConversationDao;
import code.modules.conversation.service.Response;
import code.modules.conversation.service.Section;
import code.modules.conversation.util.ConversationMapper;
import code.util.RepositoryAdapter;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@RepositoryAdapter
@AllArgsConstructor
public class ConversationRepository implements ConversationDao {

  private final ResponseJpaRepo responseJpaRepo;
  private ConversationJpaRepo conversationJpaRepo;
  private SectionJpaRepo sectionJpaRepo;
  private ConversationMapper mapper;

  @Override
  @Transactional
  public Section create(Response response) {
    ResponseEntity entity = mapper.domainToEntity(response);
    ResponseEntity saved = responseJpaRepo.save(entity);
    return mapper.entityToDomain(saved.getRequest().getSection());
  }
  @Override
  @Transactional(readOnly = true) // TODO cqrs in repo adapter layer
  public Page<Section> getSectionPage(PageRequest pageRequest, Conversation conversation) {
    ConversationEntity entity = mapper.domainToEntity(conversation);
    Page<SectionNavigationProjection> page = sectionJpaRepo
      .findSectionsByConversationIdWithSelectedRequestAndResponse(entity, pageRequest);
    return page.map(projection -> mapper.entityToDomain(projection));
  }

  @Override
  @Transactional
  public Conversation create(Conversation conversation) {
    ConversationEntity entity = mapper.domainToEntity(conversation);
    ConversationEntity saved = conversationJpaRepo.save(entity);
    return mapper.entityToDomain(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<Conversation> getConversationPage(PageRequest pageRequest, UUID accountId) {
    Page<ConversationEntity> page = conversationJpaRepo.findByAccountId(accountId, pageRequest);
    return page.map(mapper::entityToDomain);
  }

}