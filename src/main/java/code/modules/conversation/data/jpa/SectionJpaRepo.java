package code.modules.conversation.data.jpa;

import code.modules.conversation.data.ConversationEntity;
import code.modules.conversation.data.SectionEntity;
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
    SELECT s AS section,
           reqSelected AS selectedRequest,
           (SELECT reqPrev.id 
            FROM RequestEntity reqPrev 
            WHERE reqPrev.section = s 
              AND reqPrev.created < reqSelected.created 
            ORDER BY reqPrev.created DESC, reqPrev.id DESC 
            LIMIT 1) AS prevRequestId,
           (SELECT reqNext.id 
            FROM RequestEntity reqNext 
            WHERE reqNext.section = s 
              AND reqNext.created > reqSelected.created 
            ORDER BY reqNext.created ASC, reqNext.id ASC 
            LIMIT 1) AS nextRequestId,
           resSelected AS selectedResponse,
           (SELECT resPrev.id 
            FROM ResponseEntity resPrev 
            WHERE resPrev.request = reqSelected 
              AND resPrev.created < resSelected.created 
            ORDER BY resPrev.created DESC, resPrev.id DESC 
            LIMIT 1) AS prevResponseId,
           (SELECT resNext.id 
            FROM ResponseEntity resNext 
            WHERE resNext.request = reqSelected 
              AND resNext.created > resSelected.created 
            ORDER BY resNext.created ASC, resNext.id ASC 
            LIMIT 1) AS nextResponseId
    FROM SectionEntity s
    LEFT JOIN RequestEntity reqSelected ON reqSelected.section = s AND reqSelected.selected = true
    LEFT JOIN ResponseEntity resSelected ON resSelected.request = reqSelected AND resSelected.selected = true
    WHERE s.conversation = :conversation
""")
  Page<SectionNavigationProjection> findSectionWithSelectedRequestAndResponse(
    @Param("conversation") ConversationEntity conversation,
    Pageable pageable
  );

  @Query("""
    SELECT s AS section,
           reqSelected AS selectedRequest,
           (SELECT reqPrev.id 
            FROM RequestEntity reqPrev 
            WHERE reqPrev.section = s 
              AND reqPrev.created < reqSelected.created 
            ORDER BY reqPrev.created DESC, reqPrev.id DESC 
            LIMIT 1) AS prevRequestId,
           (SELECT reqNext.id 
            FROM RequestEntity reqNext 
            WHERE reqNext.section = s 
              AND reqNext.created > reqSelected.created 
            ORDER BY reqNext.created ASC, reqNext.id ASC 
            LIMIT 1) AS nextRequestId,
           resSelected AS selectedResponse,
           (SELECT resPrev.id 
            FROM ResponseEntity resPrev 
            WHERE resPrev.request = reqSelected 
              AND resPrev.created < resSelected.created 
            ORDER BY resPrev.created DESC, resPrev.id DESC 
            LIMIT 1) AS prevResponseId,
           (SELECT resNext.id 
            FROM ResponseEntity resNext 
            WHERE resNext.request = reqSelected 
              AND resNext.created > resSelected.created 
            ORDER BY resNext.created ASC, resNext.id ASC 
            LIMIT 1) AS nextResponseId
        FROM SectionEntity s
        LEFT JOIN RequestEntity reqSelected ON reqSelected.section = s AND reqSelected.selected = true
        LEFT JOIN ResponseEntity resSelected ON resSelected.request = reqSelected AND resSelected.selected = true
        WHERE s = :section
    """)
  SectionNavigationProjection findBySection(@Param("section") SectionEntity section);

  int deleteByIdAndConversationAccountId(UUID sectionId, UUID accountId);
}