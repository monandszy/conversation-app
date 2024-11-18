package code.modules.conversation.data.jpa.projection;

import code.modules.conversation.data.entity.ResponseEntity;
import java.util.UUID;

public record ResponseWindow(
  ResponseEntity selectedResponse,
  UUID prevResponseId,
  UUID nextResponseId
) {}