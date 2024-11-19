package code.modules.conversation.data.entity;

import code.modules.conversation.service.domain.Request;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "requests")
public class  RequestEntity {
  @EmbeddedId
  @AttributeOverride(name = "value", column = @Column(name = "id"))
  private Request.RequestId id;

  @Column(name = "text")
  private String text;

  @Column(name = "created", updatable = false)
  private OffsetDateTime created;

  @Column(name = "selected")
  private Boolean selected;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "section_id")
  private SectionEntity section;

  @OneToMany(mappedBy = "request", fetch = FetchType.LAZY,
    cascade = {CascadeType.REMOVE}
  )
  private final List<ResponseEntity> responses = new ArrayList<>();
}