package code.modules.conversation.service;

import code.modules.conversation.IConversationQueryFacade.ConversationData;
import code.modules.conversation.service.domain.AccountId;
import code.modules.conversation.service.domain.Conversation;
import code.modules.conversation.service.domain.Request;
import code.modules.conversation.service.domain.Response;
import code.modules.conversation.service.domain.Section;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ReadConversationDao {
  Page<Conversation> getConversationPage(PageRequest pageRequest, AccountId accountId);

  Page<Conversation> getConversationPageWithFilter(Conversation.ConversationId conversationId, AccountId accountId);

  Page<Section> getSectionPage(PageRequest pageRequest, Conversation.ConversationId conversationId);

  Request getRequestWithNavigation(Request.RequestId requestId, Section.SectionId sectionId);

  Response getResponseWithNavigation(Response.ResponseId responseId, Request.RequestId requestId);

  Request getRequest(Request.RequestId requestId);

  ConversationData getConversationData(Conversation.ConversationId conversationId);
}