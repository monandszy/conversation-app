package code.modules.conversation.data.jpa;

import code.modules.conversation.data.entity.RequestEntity;
import code.modules.conversation.data.entity.ResponseEntity;
import code.modules.conversation.data.jpa.projection.ResponseWindow;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponseJpaRepo extends JpaRepository<ResponseEntity, UUID> {

  @Query("""
      SELECT
      new code.modules.conversation.data.jpa.projection.ResponseWindow(
        res,
        LAG(res.id) OVER (PARTITION BY res.request ORDER BY res.created),
        LEAD(res.id) OVER (PARTITION BY res.request ORDER BY res.created)
      )
      FROM ResponseEntity res
      WHERE res = :selectedResponse
    """)
  ResponseWindow findProjectionByResponse(@Param("selectedResponse") ResponseEntity selectedResponse);

  int deleteByIdAndRequestSectionConversationAccountId(UUID responseId, UUID accountId);

  @Modifying
  @Query("UPDATE ResponseEntity e " +
    "SET e.selected = CASE WHEN e = :response THEN true ELSE false END " +
    "WHERE (e.selected = true OR e = :response) AND e.request = :request")
  void deselectAndSelect(RequestEntity request, ResponseEntity response);
}