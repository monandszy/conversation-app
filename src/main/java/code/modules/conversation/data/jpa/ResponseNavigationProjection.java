package code.modules.conversation.data.jpa;

import code.modules.conversation.data.ResponseEntity;
import java.util.UUID;

public interface ResponseNavigationProjection {
  ResponseEntity getSelectedResponse();
  UUID getPrevResponseId();
  UUID getNextResponseId();
}