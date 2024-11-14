package code.modules.conversation.service;

import code.modules.conversation.service.domain.Conversation;
import code.modules.conversation.service.domain.Request;
import code.modules.conversation.service.domain.Response;
import code.modules.conversation.service.domain.Section;
import java.util.UUID;

public interface CommandConversationDao {

  Conversation create(Conversation conversation);

  Section create(Section section, Request request, Response response);

  Request create(Request request, Response response);

  Response create(Response response);

  void deleteConversation(Conversation conversation, UUID accountId);

  void deleteSection(Section section, UUID accountId);

  void deleteRequest(Request request, UUID accountId);

  void deleteResponse(Response response, UUID accountId);
}