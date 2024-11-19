package code.modules.conversation.data.jpa;

import static code.modules.conversation.service.domain.Conversation.ConversationId;

import code.modules.conversation.data.entity.ConversationEntity;
import code.modules.conversation.service.domain.AccountId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationJpaRepo extends JpaRepository<ConversationEntity, ConversationId> {

  Page<ConversationEntity> findByAccountId(AccountId accountId, Pageable pageRequest);

  boolean existsByIdAndAccountId(ConversationId id, AccountId accountId);

}