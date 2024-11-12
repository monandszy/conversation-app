package code.modules.conversation.data.jpa;

import code.modules.conversation.data.RequestEntity;
import code.modules.conversation.data.SectionEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RequestJpaRepo extends JpaRepository<RequestEntity, UUID> {

  @Modifying
  @Query("UPDATE RequestEntity e SET e.selected = false WHERE e.selected = true AND e.section = :section")
  void deselectOther(SectionEntity section);

  @Query("""
    SELECT req AS selectedRequest,
           (SELECT reqPrev.id 
            FROM RequestEntity reqPrev 
            WHERE reqPrev.section = req.section 
              AND reqPrev.created < req.created 
            ORDER BY reqPrev.created DESC, reqPrev.id DESC 
            LIMIT 1) AS prevRequestId,
           (SELECT reqNext.id 
            FROM RequestEntity reqNext 
            WHERE reqNext.section = req.section 
              AND reqNext.created > req.created 
            ORDER BY reqNext.created ASC, reqNext.id ASC 
            LIMIT 1) AS nextRequestId,
           resSelected AS selectedResponse,
           (SELECT resPrev.id 
            FROM ResponseEntity resPrev 
            WHERE resPrev.request = req 
              AND resPrev.created < resSelected.created 
            ORDER BY resPrev.created DESC, resPrev.id DESC 
            LIMIT 1) AS prevResponseId,
           (SELECT resNext.id 
            FROM ResponseEntity resNext 
            WHERE resNext.request = req 
              AND resNext.created > resSelected.created 
            ORDER BY resNext.created ASC, resNext.id ASC 
            LIMIT 1) AS nextResponseId
    FROM RequestEntity req
    LEFT JOIN ResponseEntity resSelected ON resSelected.request = req AND resSelected.selected = true
    WHERE req = :selectedRequest
""")
  RequestNavigationProjection findByRequest(@Param("selectedRequest") RequestEntity selectedRequest);

  int deleteByIdAndSectionConversationAccountId(UUID requestId, UUID accountId);

}