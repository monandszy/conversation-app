package code.modules.conversation.data.jpa;

import code.modules.conversation.data.entity.SectionEntity;
import code.modules.conversation.service.domain.AccountId;
import code.modules.conversation.service.domain.Conversation;
import code.modules.conversation.service.domain.Section;
import jakarta.persistence.Tuple;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SectionJpaRepo extends JpaRepository<SectionEntity, Section.SectionId> {

  @Query("""
    SELECT
      res.id.value,
      TO_CHAR(res.created, 'YYYY-MM-DD"T"HH24:MI:SS.US"Z"'),
      res.text,
      LAG(res.id.value) OVER (PARTITION BY req.id ORDER BY res.created),
      LEAD(res.id.value) OVER (PARTITION BY req.id ORDER BY res.created),
      req.id.value,
      TO_CHAR(req.created, 'YYYY-MM-DD"T"HH24:MI:SS.US"Z"'),
      req.text,
      LAG(req.id.value) OVER (PARTITION BY s.id ORDER BY req.created),
      LEAD(req.id.value) OVER (PARTITION BY s.id ORDER BY req.created),
      s.id.value,
      TO_CHAR(s.created, 'YYYY-MM-DD"T"HH24:MI:SS.US"Z"')
    FROM SectionEntity s
    LEFT JOIN RequestEntity req ON req.section.id = s.id AND req.selected = true
    LEFT JOIN ResponseEntity res ON res.request.id = req.id AND res.selected = true
    WHERE s.conversation.id = :conversationId
    ORDER BY s.created
    LIMIT :pageSize OFFSET :offset
    """)
  List<Object[]> findProjectionPageByConversationId(
    @Param("conversationId") Conversation.ConversationId conversationId,
    Integer pageSize, Integer offset
  );

  @Query("""
    SELECT
      res.id.value,
      TO_CHAR(res.created, 'YYYY-MM-DD"T"HH24:MI:SS.US"Z"'),
      res.text,
      LAG(res.id.value) OVER (PARTITION BY req.id ORDER BY res.created),
      LEAD(res.id.value) OVER (PARTITION BY req.id ORDER BY res.created),
      req.id.value,
      TO_CHAR(req.created, 'YYYY-MM-DD"T"HH24:MI:SS.US"Z"'),
      req.text,
      LAG(req.id.value) OVER (PARTITION BY s.id ORDER BY req.created),
      LEAD(req.id.value) OVER (PARTITION BY s.id ORDER BY req.created),
      s.id.value,
      TO_CHAR(s.created, 'YYYY-MM-DD"T"HH24:MI:SS.US"Z"')
    FROM SectionEntity s
    LEFT JOIN RequestEntity req ON req.section.id = s.id AND req.selected = true
    LEFT JOIN ResponseEntity res ON res.request.id = req.id AND res.selected = true
    WHERE s.id = :sectionId
    """)
  Object[] findProjectionBySectionId(@Param("sectionId") Section.SectionId sectionId);

  boolean existsByIdAndConversationAccountId(Section.SectionId sectionId, AccountId accountId);

  @Query("SELECT " +
    "(SELECT COUNT(s) FROM SectionEntity s WHERE s.conversation.id = :conversationId) AS sectionCount, " +
    "(SELECT COUNT(r) FROM RequestEntity r WHERE r.section.conversation.id = :conversationId) AS requestCount, " +
    "(SELECT COUNT(resp) FROM ResponseEntity resp WHERE resp.request.section.conversation.id = :conversationId) AS responseCount")
  Tuple countSectionsRequestsResponses(@Param("conversationId") Conversation.ConversationId conversationId);

  long countByConversationId(Conversation.ConversationId conversationId);
}