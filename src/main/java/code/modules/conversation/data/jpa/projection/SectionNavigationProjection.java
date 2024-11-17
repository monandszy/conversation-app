package code.modules.conversation.data.jpa.projection;

import code.modules.conversation.data.entity.RequestEntity;
import code.modules.conversation.data.entity.ResponseEntity;
import code.modules.conversation.data.entity.SectionEntity;
import java.util.UUID;

public interface SectionNavigationProjection {
  SectionEntity getSection();

  RequestEntity getSelectedRequest();

  UUID getPrevRequestId();

  UUID getNextRequestId();

  ResponseEntity getSelectedResponse();

  UUID getPrevResponseId();

  UUID getNextResponseId();
}