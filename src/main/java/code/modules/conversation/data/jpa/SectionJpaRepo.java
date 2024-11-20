package code.modules.conversation.data.jpa;

import code.modules.conversation.data.entity.SectionEntity;
import code.modules.conversation.service.domain.AccountId;
import code.modules.conversation.service.domain.Conversation;
import code.modules.conversation.service.domain.Section;
import jakarta.persistence.Tuple;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SectionJpaRepo extends JpaRepository<SectionEntity, Section.SectionId> {

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
          ROW_NUMBER() OVER (PARTITION BY res.request_id ORDER BY res.created) AS position,
          res.request_id AS request_id
        FROM responses res
        ),
      request_window AS (
        SELECT
          req.id AS id,
          req.selected AS selected,
          TO_CHAR(req.created, 'YYYY-MM-DD"T"HH24:MI:SS.US"Z"') AS created,
          req.text AS text,
          LAG(req.id) OVER (PARTITION BY req.section_id ORDER BY req.created) AS prev,
          LEAD(req.id) OVER (PARTITION BY req.section_id ORDER BY req.created) AS next,
          COUNT(*) OVER (PARTITION BY req.section_id) AS total,
          ROW_NUMBER() OVER (PARTITION BY req.section_id ORDER BY req.created) AS position,
          req.section_id AS section_id
        FROM requests req
      )
      SELECT
        resw.id,
        resw.created,
        resw.text,
        resw.prev,
        resw.next,
        resw.total,
        resw.position,
        reqw.id,
        reqw.created,
        reqw.text,
        reqw.prev,
        reqw.next,
        reqw.total,
        reqw.position,
        s.id,
        TO_CHAR(s.created, 'YYYY-MM-DD"T"HH24:MI:SS.US"Z"')
      FROM sections s
      LEFT JOIN request_window reqw ON reqw.section_id = s.id AND reqw.selected = true
      LEFT JOIN response_window resw ON resw.request_id = reqw.id AND resw.selected = true
      WHERE s.conversation_id = :conversationId
      ORDER BY s.created
      LIMIT :pageSize OFFSET :offset
    """, nativeQuery = true)
  List<Object[]> findProjectionPageByConversationId(
    UUID conversationId,
    Integer pageSize, Integer offset
  );

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
          ROW_NUMBER() OVER (PARTITION BY res.request_id ORDER BY res.created) AS position,
          res.request_id AS request_id
        FROM responses res
      ),
      request_window AS (
        SELECT
          req.id AS id,
          req.selected AS selected,
          TO_CHAR(req.created, 'YYYY-MM-DD"T"HH24:MI:SS.US"Z"') AS created,
          req.text AS text,
          LAG(req.id) OVER (PARTITION BY req.section_id ORDER BY req.created) AS prev,
          LEAD(req.id) OVER (PARTITION BY req.section_id ORDER BY req.created) AS next,
          COUNT(*) OVER (PARTITION BY req.section_id) AS total,
          ROW_NUMBER() OVER (PARTITION BY req.section_id ORDER BY req.created) AS position,
          req.section_id AS section_id
        FROM requests req
      )
      SELECT
        resw.id,
        resw.created,
        resw.text,
        resw.prev,
        resw.next,
        resw.total,
        resw.position,
        reqw.id,
        reqw.created,
        reqw.text,
        reqw.prev,
        reqw.next,
        reqw.total,
        reqw.position,
        s.id,
        TO_CHAR(s.created, 'YYYY-MM-DD"T"HH24:MI:SS.US"Z"')
    FROM sections s
    LEFT JOIN request_window reqw ON reqw.section_id = s.id AND reqw.selected = TRUE
    LEFT JOIN response_window resw ON resw.request_id = reqw.id AND resw.selected = TRUE
    WHERE s.id = :sectionId
    """, nativeQuery = true)
  Object[] findProjectionBySectionId(UUID sectionId);

  boolean existsByIdAndConversationAccountId(Section.SectionId sectionId, AccountId accountId);

  @Query("SELECT " +
    "(SELECT COUNT(s) FROM SectionEntity s WHERE s.conversation.id = :conversationId) AS sectionCount, " +
    "(SELECT COUNT(r) FROM RequestEntity r WHERE r.section.conversation.id = :conversationId) AS requestCount, " +
    "(SELECT COUNT(resp) FROM ResponseEntity resp WHERE resp.request.section.conversation.id = :conversationId) AS responseCount")
  Tuple countSectionsRequestsResponses(@Param("conversationId") Conversation.ConversationId conversationId);

  long countByConversationId(Conversation.ConversationId conversationId);
}