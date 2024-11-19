package code.modules.conversation.data.entity;

import code.modules.conversation.service.domain.Response;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
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
  @EmbeddedId
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @AttributeOverride(name = "value", column = @Column(name = "id"))
  private Response.ResponseId id;

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