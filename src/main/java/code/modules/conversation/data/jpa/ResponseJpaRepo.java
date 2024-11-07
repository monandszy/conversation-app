package code.modules.conversation.data.jpa;

import code.modules.conversation.data.ResponseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ResponseJpaRepo extends JpaRepository<ResponseEntity, UUID> {
}