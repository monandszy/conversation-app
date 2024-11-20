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
        FLOOR(selected_position / :pageSize)\\:\\:int AS page_number,
        (FLOOR(selected_position / :pageSize) * :pageSize) AS min_position,
        (LEAST(CEIL(selected_position / :pageSize) * :pageSize, total_amount)) AS max_position,
        total_amount,
        selected_position
      FROM (
        SELECT
          c.id AS id,
          ROW_NUMBER() OVER (ORDER BY c.created DESC)\\:\\:float AS selected_position
        FROM conversations c
        WHERE c.account_id = :accountId
      ) selected,
      (
        SELECT COUNT(*) AS total_amount
        FROM conversations c
        WHERE c.account_id = :accountId
      ) total
      WHERE selected.id = :conversationId
    )
    SELECT
        pr.page_number,
        pr.total_amount,
        array_agg(agg_data.aggregated_data)
    FROM position_range pr
    CROSS JOIN LATERAL (
        SELECT
            c.id || ',' || TO_CHAR(c.created, 'YYYY-MM-DD"T"HH24:MI:SS.US"Z"') AS aggregated_data
        FROM conversations c
        WHERE c.account_id = :accountId
        ORDER BY c.created DESC
        LIMIT :pageSize OFFSET (SELECT min_position FROM position_range)
    ) agg_data
    GROUP BY
        pr.page_number,
        pr.total_amount
    """, nativeQuery = true)
  Object[] findByAccountIdWithFiler(
    UUID accountId,
    UUID conversationId,
    Integer pageSize
  );

  /*    GROUP BY
      pr.page_number,
      pr.total_amount
      array_agg((c.id || ',' || TO_CHAR(c.created, 'YYYY-MM-DD"T"HH24:MI:SS.US"Z"')) ORDER BY c.created DESC),*/
}