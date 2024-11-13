package code.modules.conversation.service;

import code.modules.conversation.ConversationQueryFacadeI.ConversationData;
import code.modules.conversation.service.domain.Conversation;
import code.modules.conversation.service.domain.Request;
import code.modules.conversation.service.domain.Response;
import code.modules.conversation.service.domain.Section;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ReadConversationDao {
  Page<Conversation> getConversationPage(PageRequest pageRequest, UUID accountId);

  Page<Section> getSectionPage(PageRequest pageRequest, Conversation conversation);

  Request getRequestWithNavigation(Request request, Section section);

  Response getResponseWithNavigation(Response response, Request request);

  Request getRequest(Request request);

  Conversation getConversation(Conversation conversation);

  ConversationData getConversationData(Conversation conversation);
}