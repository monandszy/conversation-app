package code.modules.conversation.data.jpa.projection;

import code.modules.conversation.data.entity.ResponseEntity;
import java.util.UUID;

public interface ResponseNavigationProjection {
  ResponseEntity getSelectedResponse();

  UUID getPrevResponseId();

  UUID getNextResponseId();
}