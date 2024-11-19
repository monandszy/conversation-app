package code.modules.conversation;

import code.modules.conversation.data.ValidationRepo;
import java.security.Principal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component("ownershipValidator")
@AllArgsConstructor
public class OwnershipValidator {

  private ValidationRepo validationRepo;

  public boolean validateConversation(Principal principal, String provided) {
    UUID conversationId = UUID.fromString(provided);
    UUID accountId = UUID.fromString(principal.getName());
    return validationRepo.validateConversation(conversationId, accountId);
  }

  public boolean validateSection(Principal principal, String provided) {
    UUID sectionId = UUID.fromString(provided);
    UUID accountId = UUID.fromString(principal.getName());
    return validationRepo.validateSection(sectionId, accountId);
  }

  public boolean validateRequest(Principal principal, String provided) {
    UUID requestId = UUID.fromString(provided);
    UUID accountId = UUID.fromString(principal.getName());
    return validationRepo.validateRequest(requestId, accountId);
  }

  public boolean validateResponse(Principal principal, String provided) {
    UUID responseId = UUID.fromString(provided);
    UUID accountId = UUID.fromString(principal.getName());
    return validationRepo.validateResponse(responseId, accountId);
  }
}