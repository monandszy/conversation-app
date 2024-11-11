package code.modules.conversation.data.jpa;

import code.modules.conversation.data.RequestEntity;
import code.modules.conversation.data.ResponseEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponseJpaRepo extends JpaRepository<ResponseEntity, UUID> {

  @Modifying
  @Query("UPDATE ResponseEntity e SET e.selected = false WHERE e.selected = true AND e.request = :request ")
  void deselectOther(RequestEntity request);

  @Query("""
    SELECT res AS selectedResponse,
           (SELECT resPrev.id
            FROM ResponseEntity resPrev
            WHERE resPrev.request = res.request 
              AND resPrev.created < res.created
            ORDER BY resPrev.created DESC, resPrev.id DESC
            LIMIT 1) AS prevResponseId,
           (SELECT resNext.id
            FROM ResponseEntity resNext
            WHERE resNext.request = res.request 
              AND resNext.created > res.created
            ORDER BY resNext.created ASC, resNext.id ASC
            LIMIT 1) AS nextResponseId
    FROM ResponseEntity res
    WHERE res = :selectedResponse
""")
  ResponseNavigationProjection findByResponse(@Param("selectedResponse") ResponseEntity selectedResponse);



}