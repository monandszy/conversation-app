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
      WITH request_window AS (
        SELECT
          res.id,
          res.created\\:\\:timestampz,
          res.text,
          LAG(res.id) OVER (PARTITION BY req.id ORDER BY res.created),
          LEAD(res.id) OVER (PARTITION BY req.id ORDER BY res.created),
          req.id AS sreqid,
          req.created\\:\\:timestampz,
          req.text,
          LAG(req.id) OVER (PARTITION BY req.section_id ORDER BY req.created),
          LEAD(req.id) OVER (PARTITION BY req.section_id ORDER BY req.created)
        FROM requests req
        LEFT JOIN responses res ON res.request_id = req.id AND res.selected = TRUE
      )
      SELECT
          rw.*
      FROM request_window rw
      WHERE rw.sreqid = :selectedRequestId
    """, nativeQuery = true)
  Object[] findProjectionByRequest(@Param("selectedRequestId") UUID selectedRequestId);

  @Modifying
  @Query("UPDATE RequestEntity e " +
    "SET e.selected = CASE WHEN e.id = :requestId THEN true ELSE false END " +
    "WHERE e.selected = true OR e.id = :requestId AND e.section.id = :sectionId")
  void deselectAndSelect(@Param("sectionId") SectionId sectionId, @Param("requestId") RequestId requestId);

  boolean existsByIdAndSectionConversationAccountId(RequestId requestId, AccountId accountId);

}