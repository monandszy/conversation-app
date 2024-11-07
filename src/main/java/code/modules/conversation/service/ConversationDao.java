package code.modules.conversation.service;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ConversationDao {

  Request create(Request request);

  Conversation create(Conversation conversation);

  Page<Conversation> getConversationPage(PageRequest pageRequest, UUID accountId);

  Page<Request> getRequestPage(PageRequest pageRequest, Conversation conversation);
}