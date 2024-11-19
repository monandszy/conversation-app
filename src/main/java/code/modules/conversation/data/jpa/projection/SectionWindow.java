package code.modules.conversation.data.jpa.projection;

import code.modules.conversation.data.entity.RequestEntity;
import code.modules.conversation.data.entity.ResponseEntity;
import code.modules.conversation.data.entity.SectionEntity;
import code.modules.conversation.service.domain.Request;
import code.modules.conversation.service.domain.Response;

public record SectionWindow(
  RequestEntity selectedRequest,
  SectionEntity section,
  Request.RequestId prevRequestId,
  Request.RequestId nextRequestId,
  ResponseEntity selectedResponse,
  Response.ResponseId prevResponseId,
  Response.ResponseId nextResponseId
) {}