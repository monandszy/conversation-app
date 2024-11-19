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
public class Conversation {
  ConversationId id;
  OffsetDateTime created;
  AccountId accountId;

  @Embeddable
  public record ConversationId(UUID value) implements Serializable {
    public static ConversationId generate() {
      return new ConversationId(UUID.randomUUID());
    }
  }
}