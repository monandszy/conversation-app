package code.modules.conversation;

import static code.modules.conversation.service.domain.Conversation.ConversationId;
import static code.modules.conversation.service.domain.Request.RequestId;
import static code.modules.conversation.service.domain.Response.ResponseId;
import static code.modules.conversation.service.domain.Section.SectionId;

import code.modules.conversation.data.ValidationRepo;
import code.modules.conversation.service.domain.AccountId;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service("ownershipValidator")
@AllArgsConstructor
public class OwnershipValidator {

  private ValidationRepo validationRepo;

  public boolean validateConversation(User principal, UUID provided) {
    return validationRepo.validateConversation(
      new ConversationId(provided), new AccountId(UUID.fromString(principal.getUsername()))
    );
  }

  public boolean validateSection(org.springframework.security.core.userdetails.User principal, UUID provided) {
    return validationRepo.validateSection(
      new SectionId(provided), new AccountId(UUID.fromString(principal.getUsername()))
    );
  }

  public boolean validateRequest(User principal, UUID provided) {
    return validationRepo.validateRequest(
      new RequestId(provided), new AccountId(UUID.fromString(principal.getUsername()))
    );
  }

  public boolean validateResponse(User principal, UUID provided) {
    return validationRepo.validateResponse(
      new ResponseId(provided), new AccountId(UUID.fromString(principal.getUsername()))
    );
  }
}