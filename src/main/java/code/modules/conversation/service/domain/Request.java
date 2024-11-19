package code.modules.conversation.service.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;
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
public class Request {
  RequestId id;
  String text;
  Boolean selected;
  OffsetDateTime created;
  Section section;
  List<Response> responses;
  RequestNavigation navigation;

  @Embeddable
  public record RequestId(UUID value) implements Serializable {}

  public record RequestNavigation(
    Request.RequestId nextId,
    Request.RequestId previousId) {
  }
}