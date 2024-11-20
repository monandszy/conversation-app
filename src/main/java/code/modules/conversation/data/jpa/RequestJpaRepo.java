package code.modules.conversation.data.jpa;

import static code.modules.conversation.service.domain.Request.RequestId;

import code.modules.conversation.data.entity.RequestEntity;
import code.modules.conversation.service.domain.AccountId;
import code.modules.conversation.service.domain.Section.SectionId;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RequestJpaRepo extends JpaRepository<RequestEntity, RequestId> {

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
        WHERE req.section_id = :sectionId
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
        reqw.position
      FROM request_window reqw
      LEFT JOIN response_window resw ON resw.request_id = reqw.id AND resw.selected = true
      WHERE reqw.id = :requestId AND reqw.selected = TRUE
    """, nativeQuery = true)
  Object[] findProjectionByRequest(UUID requestId, UUID sectionId);

  @Modifying
  @Query("UPDATE RequestEntity e " +
         "SET e.selected = CASE WHEN e.id = :requestId THEN true ELSE false END " +
         "WHERE e.selected = true OR e.id = :requestId AND e.section.id = :sectionId")
  void deselectAndSelect(@Param("sectionId") SectionId sectionId, @Param("requestId") RequestId requestId);

  boolean existsByIdAndSectionConversationAccountId(RequestId requestId, AccountId accountId);

}