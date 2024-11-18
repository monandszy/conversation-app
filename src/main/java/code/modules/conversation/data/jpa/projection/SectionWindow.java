package code.modules.conversation.data.jpa.projection;

import code.modules.conversation.data.entity.RequestEntity;
import code.modules.conversation.data.entity.ResponseEntity;
import code.modules.conversation.data.entity.SectionEntity;
import java.util.UUID;

public record SectionWindow(
  SectionEntity section,
  RequestEntity selectedRequest,
  UUID prevRequestId,
  UUID nextRequestId,
  ResponseEntity selectedResponse,
  UUID prevResponseId,
  UUID nextResponseId
) {}