package code.modules.conversation.data.jpa;

import code.modules.conversation.data.RequestEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestJpaRepo extends JpaRepository<RequestEntity, UUID> {
}