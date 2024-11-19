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
public class Section {
  SectionId id;
  OffsetDateTime created;
  Conversation conversation;
  List<Request> requests;

  @Embeddable
  public record SectionId(UUID value) implements Serializable {
    public static SectionId generate() {
      return new SectionId(UUID.randomUUID());
    }
  }
}