package code.modules.conversation.data.jpa.projection;

import code.modules.conversation.service.domain.Request;
import code.modules.conversation.service.domain.Response;
import code.modules.conversation.service.domain.Section;
import java.time.Instant;

public record SectionWindow(
  Response.ResponseId responseId,
  Instant responseCreated,
  String responseText,
  Response.ResponseId prevResponseId,
  Response.ResponseId nextResponseId,
  Request.RequestId requestId,
  Instant requestCreated,
  String requestText,
  Request.RequestId prevRequestId,
  Request.RequestId nextRequestId,
  Section.SectionId sectionId,
  Instant sectionCreated
) {}