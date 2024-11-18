package code.modules.conversation.data.jpa.projection;

import code.modules.conversation.data.entity.RequestEntity;
import code.modules.conversation.data.entity.ResponseEntity;
import java.util.UUID;

public record RequestWindow(
  RequestEntity selectedRequest,
  UUID prevRequestId,
  UUID nextRequestId,
  ResponseEntity selectedResponse,
  UUID prevResponseId,
  UUID nextResponseId
) {}