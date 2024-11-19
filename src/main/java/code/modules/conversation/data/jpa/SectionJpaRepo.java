package code.modules.conversation.data.jpa;

import code.modules.conversation.data.entity.SectionEntity;
import code.modules.conversation.data.jpa.projection.SectionWindow;
import code.modules.conversation.service.domain.AccountId;
import code.modules.conversation.service.domain.Conversation;
import code.modules.conversation.service.domain.Section;
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
        LAG(req.id) OVER (PARTITION BY s.id ORDER BY req.created),
        LEAD(req.id) OVER (PARTITION BY s.id ORDER BY req.created),
        res,
        LAG(res.id) OVER (PARTITION BY req.id ORDER BY res.created),
        LEAD(res.id) OVER (PARTITION BY req.id ORDER BY res.created)
      )
    FROM SectionEntity s
    LEFT JOIN RequestEntity req ON req.section = s AND req.selected = true
    LEFT JOIN ResponseEntity res ON res.request = req AND res.selected = true
    WHERE s.conversation.id = :conversationId
    """)
  Page<SectionWindow> findProjectionPageByConversationId(
    @Param("conversationId") Conversation.ConversationId conversationId,
    Pageable pageable
  );

  @Query("""
    SELECT
      new code.modules.conversation.data.jpa.projection.SectionWindow(
        s,
        req,
        LAG(req.id) OVER (PARTITION BY s.id ORDER BY req.created),
        LEAD(req.id) OVER (PARTITION BY s.id ORDER BY req.created),
        res,
        LAG(res.id) OVER (PARTITION BY req.id ORDER BY res.created),
        LEAD(res.id) OVER (PARTITION BY req.id ORDER BY res.created)
      )
    FROM SectionEntity s
    LEFT JOIN RequestEntity req ON req.section = s AND req.selected = true
    LEFT JOIN ResponseEntity res ON res.request = req AND res.selected = true
    WHERE s.id = :sectionId
    """)
  SectionWindow findProjectionBySectionId(@Param("sectionId") Section.SectionId sectionId);

  boolean existsByIdAndConversationAccountId(Section.SectionId sectionId, AccountId accountId);

  @Query("SELECT " +
    "(SELECT COUNT(s) FROM SectionEntity s WHERE s.conversation.id = :conversationId) AS sectionCount, " +
    "(SELECT COUNT(r) FROM RequestEntity r WHERE r.section.conversation.id = :conversationId) AS requestCount, " +
    "(SELECT COUNT(resp) FROM ResponseEntity resp WHERE resp.request.section.conversation.id = :conversationId) AS responseCount")
  Tuple countSectionsRequestsResponses(@Param("conversationId") Conversation.ConversationId conversationId);
}