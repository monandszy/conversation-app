package code.modules.conversation.data.jpa;

import code.modules.conversation.data.entity.ResponseEntity;
import code.modules.conversation.service.domain.AccountId;
import code.modules.conversation.service.domain.Request.RequestId;
import code.modules.conversation.service.domain.Response.ResponseId;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponseJpaRepo extends JpaRepository<ResponseEntity, ResponseId> {

  @Query(value = """
      WITH response_window AS (
          SELECT
            res.id AS id,
            res.selected AS selected,
            TO_CHAR(res.created, 'YYYY-MM-DD"T"HH24:MI:SS.US"Z"') AS created,
            res.text AS text,
            LAG(res.id) OVER (PARTITION BY res.request_id ORDER BY res.created, res.id) AS prev,
            LEAD(res.id) OVER (PARTITION BY res.request_id ORDER BY res.created, res.id) AS next,
            COUNT(*) OVER (PARTITION BY res.request_id) AS total,
            ROW_NUMBER() OVER (PARTITION BY res.request_id ORDER BY res.created) AS position
          FROM responses res WHERE res.request_id = :requestId
          )
        SELECT
            resw.id,
            resw.created,
            resw.text,
            resw.prev,
            resw.next,
            resw.total,
            resw.position
        FROM response_window resw
        WHERE resw.id = :responseId AND resw.selected = true
    """, nativeQuery = true)
  Object[] findProjectionByResponse(UUID responseId, UUID requestId);

  boolean existsByIdAndRequestSectionConversationAccountId(ResponseId responseId, AccountId accountId);

  @Modifying
  @Query("UPDATE ResponseEntity e " +
    "SET e.selected = CASE WHEN e.id = :responseId THEN true ELSE false END " +
    "WHERE (e.selected = true OR e.id = :responseId) AND e.request.id = :requestId")
  void deselectAndSelect(
    @Param("requestId") RequestId requestId,
    @Param("responseId") ResponseId responseId
  );

}