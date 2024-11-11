package code.modules.conversation.data.jpa;

import code.modules.conversation.data.RequestEntity;
import code.modules.conversation.data.ResponseEntity;
import java.util.UUID;

public interface RequestNavigationProjection {
  RequestEntity getSelectedRequest();
  UUID getPrevRequestId();
  UUID getNextRequestId();

  ResponseEntity getSelectedResponse();
  UUID getPrevResponseId();
  UUID getNextResponseId();
}