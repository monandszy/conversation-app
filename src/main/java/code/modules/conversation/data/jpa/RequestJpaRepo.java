package code.modules.conversation.data.jpa;

import code.modules.conversation.data.ConversationEntity;
import code.modules.conversation.data.RequestEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestJpaRepo extends JpaRepository<RequestEntity, UUID> {

//  @EntityGraph(
//    type = EntityGraph.EntityGraphType.FETCH,
//    attributePaths = {
//      "responses"
//    })
  Page<RequestEntity> findByConversation(ConversationEntity conversation, Pageable pageRequest);

//  @EntityGraph(
//    type = EntityGraph.EntityGraphType.FETCH,
//    attributePaths = {
//      "responses"
//    })
  List<RequestEntity> findAll();
}