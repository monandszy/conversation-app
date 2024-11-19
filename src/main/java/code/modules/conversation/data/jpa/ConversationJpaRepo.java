package code.modules.conversation.data.jpa;

import static code.modules.conversation.service.domain.Conversation.ConversationId;

import code.modules.conversation.data.entity.ConversationEntity;
import code.modules.conversation.service.domain.AccountId;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationJpaRepo extends JpaRepository<ConversationEntity, ConversationId> {

  Page<ConversationEntity> findByAccountId(AccountId accountId, Pageable pageRequest);

  boolean existsByIdAndAccountId(ConversationId id, AccountId accountId);


  @Query(value = """
    WITH position_range AS (
      SELECT
        CEIL(selected_position / :pageSize)\\:\\:int AS page_number,
        (GREATEST(CEIL(selected_position / :pageSize) - 1, 0) * :pageSize) AS min_position,
        (LEAST(CEIL(selected_position / :pageSize) * :pageSize, total_amount)) AS max_position,
        total_amount,
        selected_position
      FROM (
        SELECT ROW_NUMBER() OVER (ORDER BY c.created DESC)\\:\\:float AS selected_position
        FROM conversations c
        WHERE c.id = :conversationId
      ) selected,
      (
        SELECT COUNT(*) AS total_amount
        FROM conversations c
        WHERE c.account_id = :accountId
      ) total
    )
    SELECT
      pr.page_number,
      pr.total_amount,
      array_agg((c.id || ',' || c.created) ORDER BY c.created DESC)
    FROM conversations c
    CROSS JOIN position_range pr
    WHERE c.account_id = :accountId
    GROUP BY
      pr.page_number,
      pr.total_amount
    LIMIT :pageSize
    OFFSET (SELECT min_position FROM position_range)
    """, nativeQuery = true)
  Object[] findByAccountIdWithFiler(
    UUID accountId,
    UUID conversationId,
    Integer pageSize
  );
}