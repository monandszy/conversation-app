package code.modules.conversation.data.jpa;

import code.modules.conversation.data.ConversationEntity;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationJpaRepo extends JpaRepository<ConversationEntity, UUID> {

  Page<ConversationEntity> findByAccountId(UUID accountId, Pageable pageRequest);

}