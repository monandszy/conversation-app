package code.modules.conversation.data.jpa;

import code.modules.conversation.data.entity.RequestEntity;
import code.modules.conversation.data.entity.SectionEntity;
import code.modules.conversation.data.jpa.projection.RequestWindow;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RequestJpaRepo extends JpaRepository<RequestEntity, UUID> {

  @Query("""
    SELECT
      new code.modules.conversation.data.jpa.projection.RequestWindow(
        req,
        LAG(req.id) OVER (PARTITION BY req.section ORDER BY req.created, req.id),
        LEAD(req.id) OVER (PARTITION BY req.section ORDER BY req.created, req.id),
        res,
        LAG(res.id) OVER (PARTITION BY req.id ORDER BY res.created, res.id),
        LEAD(res.id) OVER (PARTITION BY req.id ORDER BY res.created, res.id)
      )
    FROM RequestEntity req
    LEFT JOIN ResponseEntity res ON res.request = req AND res.selected = true
    WHERE req = :selectedRequest
    """)
  RequestWindow findProjectionByRequest(@Param("selectedRequest") RequestEntity selectedRequest);

  @Modifying
  @Query("UPDATE RequestEntity e " +
    "SET e.selected = CASE WHEN e = :request THEN true ELSE false END " +
    "WHERE e.selected = true OR e = :request AND e.section = :section")
  void deselectAndSelect(SectionEntity section, RequestEntity request);

  int deleteByIdAndSectionConversationAccountId(UUID requestId, UUID accountId);

}