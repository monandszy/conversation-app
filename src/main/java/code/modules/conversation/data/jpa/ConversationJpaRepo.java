package code.modules.conversation.data.jpa;

import static code.modules.conversation.service.domain.Conversation.ConversationId;

import code.modules.conversation.data.entity.ConversationEntity;
import code.modules.conversation.service.domain.AccountId;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationJpaRepo extends JpaRepository<ConversationEntity, ConversationId> {

  boolean existsByIdAndAccountId(ConversationId id, AccountId accountId);

  @Query(
    value = """
      WITH positions AS (
        SELECT
          c.id AS id,
          COUNT(*) OVER() AS total_amount,
          ROW_NUMBER() OVER (ORDER BY c.created)::FLOAT AS selected_position
        FROM conversations c
        WHERE c.account_id = :accountId
      )
      SELECT
        p.selected_position AS selected_postion,
        p.total_amount AS total_amount
      FROM positions p
      WHERE p.id = :conversationId
      """, nativeQuery = true
  )
  Object[] getSelectedPosition(UUID conversationId, UUID accountId);

  @Query(
    """
        SELECT c
        FROM ConversationEntity c
        WHERE c.accountId = :accountId
        ORDER BY c.created
        LIMIT :pageSize OFFSET :offset
      """)
  List<ConversationEntity> findContentByAccountId(
    AccountId accountId, Integer pageSize, Integer offset
  );

  long countByAccountId(AccountId accountId);

  /*
  *  FLOOR(selected_position / :pageSize)\:\:INT AS page_number,
          (FLOOR(selected_position / :pageSize) * :pageSize) AS min_position,
          (LEAST(CEIL(selected_position / :pageSize) * :pageSize, total_amount)) AS max_position,
  * */
}