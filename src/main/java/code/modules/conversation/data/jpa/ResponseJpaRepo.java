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
            res.id AS sResId,
            (res.created)\\:\\:timestampz,
            res.text,
            LAG(res.id) OVER (PARTITION BY res.request_id ORDER BY res.created, res.id),
            LEAD(res.id) OVER (PARTITION BY res.request_id ORDER BY res.created, res.id)
          FROM responses res
          )
        SELECT
            rw.*
        FROM response_window rw
        WHERE rw.sResId = :selectedResponseId
    """, nativeQuery = true)
  Object[] findProjectionByResponse(@Param("selectedResponseId") UUID selectedResponseId);

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