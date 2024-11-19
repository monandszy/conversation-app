package code.modules.conversation.data.entity;

import code.modules.conversation.service.domain.AccountId;
import code.modules.conversation.service.domain.Conversation.ConversationId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "conversations")
public class ConversationEntity {

  @EmbeddedId
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @AttributeOverride(name = "value", column = @Column(name = "id"))
  private ConversationId id;

  @Column(name = "created", updatable = false)
  private OffsetDateTime created;

  @AttributeOverride(name = "value", column = @Column(name = "account_id"))
  private AccountId accountId;

  @OneToMany(mappedBy = "conversation", fetch = FetchType.LAZY,
    cascade = {CascadeType.PERSIST, CascadeType.REMOVE}
  )
  private final List<SectionEntity> requests = new ArrayList<>();

}