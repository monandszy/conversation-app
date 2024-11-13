package code.modules.conversation.service.domain;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import lombok.With;

@Value
@With
@Builder
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
public class Response {
  UUID id;
  String text;
  Boolean selected;
  OffsetDateTime created;
  Request request;
  Navigation navigation;
}