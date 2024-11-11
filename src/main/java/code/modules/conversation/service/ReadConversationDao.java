package code.modules.conversation.service;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ReadConversationDao {
  Page<Conversation> getConversationPage(PageRequest pageRequest, UUID accountId);
  Page<Section> getSectionPage(PageRequest pageRequest, Conversation conversation);
  Request getRequestWithNavigation(UUID requestId, UUID sectionId);
  Request getRequest(UUID requestId);

  Response getResponseWithNavigation(UUID responseId, UUID requestId);
}