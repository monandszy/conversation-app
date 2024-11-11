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
           resPrev.id AS prevResponseId,
           resNext.id AS nextResponseId
    FROM ResponseEntity res
    LEFT JOIN ResponseEntity resPrev ON resPrev.created < res.created
    LEFT JOIN ResponseEntity resNext ON resNext.created > res.created

    WHERE res = :selectedResponse
""")
  ResponseNavigationProjection findByResponse(@Param("selectedResponse") ResponseEntity selectedResponse);

}