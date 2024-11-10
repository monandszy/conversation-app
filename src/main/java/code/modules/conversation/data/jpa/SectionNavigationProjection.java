package code.modules.conversation.data.jpa;

import code.modules.conversation.data.RequestEntity;
import code.modules.conversation.data.ResponseEntity;
import code.modules.conversation.data.SectionEntity;
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