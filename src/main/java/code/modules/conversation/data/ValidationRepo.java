package code.modules.conversation.data;

import static code.modules.conversation.service.domain.Request.RequestId;
import static code.modules.conversation.service.domain.Response.ResponseId;
import static code.modules.conversation.service.domain.Section.SectionId;

import code.modules.conversation.data.jpa.ConversationJpaRepo;
import code.modules.conversation.data.jpa.RequestJpaRepo;
import code.modules.conversation.data.jpa.ResponseJpaRepo;
import code.modules.conversation.data.jpa.SectionJpaRepo;
import code.modules.conversation.service.domain.AccountId;
import code.modules.conversation.service.domain.Conversation.ConversationId;
import code.util.ReadRepositoryAdapter;
import lombok.AllArgsConstructor;

@ReadRepositoryAdapter
@AllArgsConstructor
public class ValidationRepo {

  private ConversationJpaRepo conversationJpaRepo;
  private SectionJpaRepo sectionJpaRepo;
  private RequestJpaRepo requestJpaRepo;
  private ResponseJpaRepo responseJpaRepo;

    /**
   * Validates if the account has access to the given conversation.
   *
   * @param conversationId The ID of the conversation.
   * @param accountId      The ID of the account.
   * @return true if the account owns the conversation, false otherwise.
   */
  public boolean validateConversation(ConversationId conversationId, AccountId accountId) {
    return conversationJpaRepo.existsByIdAndAccountId(conversationId, accountId);
  }

    /**
   * Validates if the account has access to the given section.
   *
   * @param sectionId The ID of the section.
   * @param accountId The ID of the account.
   * @return true if the account owns the conversation that owns the section, false otherwise.
   */
  public boolean validateSection(SectionId sectionId, AccountId accountId) {
    return sectionJpaRepo.existsByIdAndConversationAccountId(sectionId, accountId);
  }

    /**
   * Validates if the account has access to the given request.
   *
   * @param requestId The ID of the request.
   * @param accountId The ID of the account.
   * @return true if the account owns the conversation that owns the section that owns the request, false otherwise.
   */
  public boolean validateRequest(RequestId requestId, AccountId accountId) {
    return requestJpaRepo.existsByIdAndSectionConversationAccountId(requestId, accountId);
  }
    /**
   * Validates if the account has access to the given response.
   *
   * @param responseId The ID of the response.
   * @param accountId  The ID of the account.
   * @return true if the account owns the conversation that owns the section that owns the request that owns the response, false otherwise.
   */
  public boolean validateResponse(ResponseId responseId, AccountId accountId) {
    return responseJpaRepo.existsByIdAndRequestSectionConversationAccountId(responseId, accountId);
  }

}