package code.modules.accounts.service.domain;

import java.util.Set;
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
public class Account {
  UUID id;
  String email;
  String password;
  Boolean enabled;
  Set<Authority> authorities;
}