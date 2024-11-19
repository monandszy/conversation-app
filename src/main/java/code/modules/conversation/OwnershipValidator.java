package code.modules.conversation;

import code.modules.conversation.data.ValidationRepo;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service("ownershipValidator")
@AllArgsConstructor
public class OwnershipValidator {

  private ValidationRepo validationRepo;

  public boolean validateConversation(User principal, UUID provided) {
    UUID accountId = UUID.fromString(principal.getUsername());
    return validationRepo.validateConversation(provided, accountId);
  }

  public boolean validateSection(org.springframework.security.core.userdetails.User principal, UUID provided) {
    UUID accountId = UUID.fromString(principal.getUsername());
    return validationRepo.validateSection(provided, accountId);
  }

  public boolean validateRequest(User principal, UUID provided) {
    UUID accountId = UUID.fromString(principal.getUsername());
    return validationRepo.validateRequest(provided, accountId);
  }

  public boolean validateResponse(User principal, UUID provided) {
    UUID accountId = UUID.fromString(principal.getUsername());
    return validationRepo.validateResponse(provided, accountId);
  }
}