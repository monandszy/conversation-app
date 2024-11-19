package code.modules.conversation.data.jpa;

import code.modules.conversation.data.entity.ConversationEntity;
import code.modules.conversation.data.entity.SectionEntity;
import code.modules.conversation.data.jpa.projection.SectionWindow;
import jakarta.persistence.Tuple;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SectionJpaRepo extends JpaRepository<SectionEntity, UUID> {

  @Query("""
    SELECT
      new code.modules.conversation.data.jpa.projection.SectionWindow(
        s,
        req,
        LAG(req.id) OVER (PARTITION BY s.id ORDER BY req.created, req.id),
        LEAD(req.id) OVER (PARTITION BY s.id ORDER BY req.created, req.id),
        res,
        LAG(res.id) OVER (PARTITION BY req.id ORDER BY res.created, res.id),
        LEAD(res.id) OVER (PARTITION BY req.id ORDER BY res.created, res.id)
      )
    FROM SectionEntity s
    LEFT JOIN RequestEntity req ON req.section = s AND req.selected = true
    LEFT JOIN ResponseEntity res ON res.request = req AND res.selected = true
    WHERE s.conversation = :conversation
    """)
  Page<SectionWindow> findProjectionPageByConversation(
    @Param("conversation") ConversationEntity conversation,
    Pageable pageable
  );

  @Query("""
    SELECT
      new code.modules.conversation.data.jpa.projection.SectionWindow(
        s,
        req,
        LAG(req.id) OVER (PARTITION BY s.id ORDER BY req.created, req.id),
        LEAD(req.id) OVER (PARTITION BY s.id ORDER BY req.created, req.id),
        res,
        LAG(res.id) OVER (PARTITION BY req.id ORDER BY res.created, res.id),
        LEAD(res.id) OVER (PARTITION BY req.id ORDER BY res.created, res.id)
      )
    FROM SectionEntity s
    LEFT JOIN RequestEntity req ON req.section = s AND req.selected = true
    LEFT JOIN ResponseEntity res ON res.request = req AND res.selected = true
    WHERE s = :section
    """)
  SectionWindow findProjectionBySection(@Param("section") SectionEntity section);

  boolean existsByIdAndConversationAccountId(UUID sectionId, UUID accountId);

  @Query("SELECT " +
    "(SELECT COUNT(s) FROM SectionEntity s WHERE s.conversation = :conversation) AS sectionCount, " +
    "(SELECT COUNT(r) FROM RequestEntity r WHERE r.section.conversation = :conversation) AS requestCount, " +
    "(SELECT COUNT(resp) FROM ResponseEntity resp WHERE resp.request.section.conversation = :conversation) AS responseCount")
  Tuple countSectionsRequestsResponses(@Param("conversation") ConversationEntity conversation);
}