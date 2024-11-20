package code.modules.conversation.service.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
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
  ResponseId id;
  String text;
  Boolean selected;
  OffsetDateTime created;
  Request request;
  ResponseNavigation navigation;

  @Embeddable
  public record ResponseId(UUID value) implements Serializable {
    public static ResponseId generate() {
      return new ResponseId(UUID.randomUUID());
    }
  }

  public record ResponseNavigation(
    Long responseCount,
    Long responsePosition,
    Response.ResponseId nextId,
    Response.ResponseId previousId
  ) {
  }
}