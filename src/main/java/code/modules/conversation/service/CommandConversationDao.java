package code.modules.conversation.service;

public interface CommandConversationDao {

  Section create(Section section, Request request, Response response);

  Request create(Request request, Response response);

  Response create(Response response);

  Conversation create(Conversation conversation);

  void delete(Conversation conversation);

  void delete(Section section);

  void delete(Request request);

  void delete(Response response);
}