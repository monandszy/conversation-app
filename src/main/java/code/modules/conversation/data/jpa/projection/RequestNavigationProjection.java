package code.modules.conversation.data.jpa.projection;

import code.modules.conversation.data.entity.RequestEntity;
import code.modules.conversation.data.entity.ResponseEntity;
import java.util.UUID;

public interface RequestNavigationProjection {
  RequestEntity getSelectedRequest();

  UUID getPrevRequestId();

  UUID getNextRequestId();

  ResponseEntity getSelectedResponse();

  UUID getPrevResponseId();

  UUID getNextResponseId();
}