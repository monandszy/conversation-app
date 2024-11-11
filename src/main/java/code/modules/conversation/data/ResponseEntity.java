package code.modules.conversation.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
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
@Table(name = "responses")
public class ResponseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private UUID id;

  @Column(name = "created", updatable = false)
  private OffsetDateTime created;

  @Column(name = "selected")
  private Boolean selected;

  @Column(name = "text")
  private String text;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "request_id")
  private RequestEntity request;
}