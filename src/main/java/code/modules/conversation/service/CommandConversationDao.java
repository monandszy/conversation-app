package code.modules.conversation.service;

import code.modules.conversation.service.domain.Conversation;
import code.modules.conversation.service.domain.Request;
import code.modules.conversation.service.domain.Response;
import code.modules.conversation.service.domain.Section;

public interface CommandConversationDao {

  Conversation create(Conversation conversation);

  Section create(Section section, Request request, Response response);

  Request create(Request request, Response response);

  Response create(Response response);

  void deleteConversation(Conversation conversation);

  void deleteSection(Section section);

  void deleteRequest(Request request);

  void deleteResponse(Response response);
}