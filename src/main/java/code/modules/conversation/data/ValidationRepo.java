package code.modules.conversation.data;

import code.modules.conversation.data.jpa.ConversationJpaRepo;
import code.modules.conversation.data.jpa.RequestJpaRepo;
import code.modules.conversation.data.jpa.ResponseJpaRepo;
import code.modules.conversation.data.jpa.SectionJpaRepo;
import code.util.ReadRepositoryAdapter;
import java.util.UUID;
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
  public boolean validateConversation(UUID conversationId, UUID accountId) {
    return conversationJpaRepo.existsByIdAndAccountId(conversationId, accountId);
  }

    /**
   * Validates if the account has access to the given section.
   *
   * @param sectionId The ID of the section.
   * @param accountId The ID of the account.
   * @return true if the account owns the conversation that owns the section, false otherwise.
   */
  public boolean validateSection(UUID sectionId, UUID accountId) {
    return sectionJpaRepo.existsByIdAndConversationAccountId(sectionId, accountId);
  }

    /**
   * Validates if the account has access to the given request.
   *
   * @param requestId The ID of the request.
   * @param accountId The ID of the account.
   * @return true if the account owns the conversation that owns the section that owns the request, false otherwise.
   */
  public boolean validateRequest(UUID requestId, UUID accountId) {
    return requestJpaRepo.existsByIdAndSectionConversationAccountId(requestId, accountId);
  }
    /**
   * Validates if the account has access to the given response.
   *
   * @param responseId The ID of the response.
   * @param accountId  The ID of the account.
   * @return true if the account owns the conversation that owns the section that owns the request that owns the response, false otherwise.
   */
  public boolean validateResponse(UUID responseId, UUID accountId) {
    return responseJpaRepo.existsByIdAndRequestSectionConversationAccountId(responseId, accountId);
  }

}