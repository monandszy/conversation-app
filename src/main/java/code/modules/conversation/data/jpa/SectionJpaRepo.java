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
               reqPrev.id AS prevRequestId, 
               reqNext.id AS nextRequestId,
               resSelected AS selectedResponse, 
               resPrev.id AS prevResponseId, 
               resNext.id AS nextResponseId
        FROM SectionEntity s
        LEFT JOIN RequestEntity reqSelected ON reqSelected.section.id = s.id AND reqSelected.selected = true
        LEFT JOIN ResponseEntity resSelected ON resSelected.request.id = reqSelected.id AND resSelected.selected = true
    
        LEFT JOIN RequestEntity reqPrev ON reqPrev.section.id = s.id AND reqPrev.created < reqSelected.created
        LEFT JOIN RequestEntity reqNext ON reqNext.section.id = s.id AND reqNext.created > reqSelected.created
        LEFT JOIN ResponseEntity resPrev ON resPrev.request.id = reqSelected.id AND resPrev.created < resSelected.created
        LEFT JOIN ResponseEntity resNext ON resNext.request.id = reqSelected.id AND resNext.created > resSelected.created
    
        WHERE s.conversation = :conversation
        ORDER BY reqPrev.created DESC, reqNext.created ASC, resPrev.created DESC, resNext.created ASC
    """)
  Page<SectionNavigationProjection> findSectionsByConversationIdWithSelectedRequestAndResponse(
    @Param("conversation") ConversationEntity conversation,
    Pageable pageable
  );
}