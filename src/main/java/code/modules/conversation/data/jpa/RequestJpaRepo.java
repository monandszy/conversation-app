package code.modules.conversation.data.jpa;

import code.modules.conversation.data.entity.RequestEntity;
import code.modules.conversation.data.entity.SectionEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RequestJpaRepo extends JpaRepository<RequestEntity, UUID> {

  @Query(value = """
    WITH request_window AS (
      SELECT
        res.id,
        res.created,
        res.text,
        LAG(res.id) OVER (PARTITION BY req.id ORDER BY res.created),
        LEAD(res.id) OVER (PARTITION BY req.id ORDER BY res.created),
        req.id AS sReqId,
        req.created,
        req.text,
        LAG(req.id) OVER (PARTITION BY req.section_id ORDER BY req.created),
        LEAD(req.id) OVER (PARTITION BY req.section_id ORDER BY req.created)
      FROM requests req
      LEFT JOIN responses res ON res.request_id = req.id AND res.selected = true
    )
    SELECT
        rw.*
    FROM request_window rw
    WHERE rw.sReqId = :selectedRequestId
""", nativeQuery = true)
  Object[] findProjectionByRequest(@Param("selectedRequestId") UUID selectedRequestId);

  @Modifying
  @Query("UPDATE RequestEntity e " +
    "SET e.selected = CASE WHEN e = :request THEN true ELSE false END " +
    "WHERE e.selected = true OR e = :request AND e.section = :section")
  void deselectAndSelect(SectionEntity section, RequestEntity request);

  boolean existsByIdAndSectionConversationAccountId(UUID requestId, UUID accountId);

}