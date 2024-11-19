package code.modules.conversation.data.jpa;

import code.modules.conversation.data.entity.RequestEntity;
import code.modules.conversation.data.entity.ResponseEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponseJpaRepo extends JpaRepository<ResponseEntity, UUID> {

  @Query(value = """
      WITH response_window AS (
          SELECT
            res.id AS sResId,
            res.created,
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

  boolean existsByIdAndRequestSectionConversationAccountId(UUID responseId, UUID accountId);

  @Modifying
  @Query("UPDATE ResponseEntity e " +
    "SET e.selected = CASE WHEN e = :response THEN true ELSE false END " +
    "WHERE (e.selected = true OR e = :response) AND e.request = :request")
  void deselectAndSelect(RequestEntity request, ResponseEntity response);
}