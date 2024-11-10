package code.modules.conversation.service;

import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import lombok.With;

@Value
@With
@Builder
@EqualsAndHashCode(of = {})
@ToString
public class Navigation {
  UUID nextId;
  UUID previousId;
}