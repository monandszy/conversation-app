package code.modules.conversation.service;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ConversationDao {

  Section create(Section item);

  Conversation create(Conversation conversation);

  Page<Conversation> getConversationPage(PageRequest pageRequest, UUID accountId);

  Page<Section> getSectionPage(PageRequest pageRequest, Conversation conversation);
}