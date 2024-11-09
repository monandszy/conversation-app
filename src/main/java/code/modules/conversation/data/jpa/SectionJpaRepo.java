package code.modules.conversation.data.jpa;

import code.modules.conversation.data.ConversationEntity;
import code.modules.conversation.data.SectionEntity;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SectionJpaRepo extends JpaRepository<SectionEntity, UUID> {

//  @EntityGraph(
//    type = EntityGraph.EntityGraphType.FETCH,
//    attributePaths = {
//      "responses"
//    })
  Page<SectionEntity> findByConversation(ConversationEntity conversation, Pageable pageRequest);
}