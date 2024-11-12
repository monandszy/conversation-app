package code.modules.conversation.service;

import java.util.UUID;

public interface CommandConversationDao {

  Section create(Section section, Request request, Response response);

  Request create(Request request, Response response);

  Response create(Response response);

  Conversation create(Conversation conversation);

  void deleteConversation(UUID conversationId, UUID accountId);

  void deleteSection(UUID sectionId, UUID accountId);

  void deleteRequest(UUID requestId, UUID accountId);

  void deleteResponse(UUID responseId, UUID accountId);
}