package code.modules.google_api.internal;

import code.configuration.SpringMapperConfig;
import code.modules.google_api.GoogleApiAdapter.ApiRequestDto;
import code.modules.google_api.GoogleApiAdapter.ApiResponseDto;
import code.openApi.model.Candidate;
import code.openApi.model.Content;
import code.openApi.model.GenerateContentRequest;
import code.openApi.model.GenerateContentResponse;
import code.openApi.model.Part;
import code.util.Generated;
import java.util.List;
import java.util.Objects;
import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = SpringMapperConfig.class)
@AnnotateWith(Generated.class)
public interface ApiModelMapper {

  @Mapping(target = "contents", source = "text", qualifiedByName = "contentMapping")
  GenerateContentRequest dtoToApiModel(ApiRequestDto requestDto);

  @Named("contentMapping")
  default List<Content> contentMapping(String text) {
    if (Objects.isNull(text)) {
      return List.of();
    }
    Part part = new Part();
    part.setText(text);

    Content content = new Content();
    content.setParts(List.of(part));
    return List.of(content);
  }

  @Mapping(target = "text", source = "candidates", qualifiedByName = "textMapping")
  ApiResponseDto apiModelToDto(GenerateContentResponse apiModel);

  @Named("textMapping")
  default String textMapping(List<Candidate> candidates) {
    Content content = candidates.getFirst().getContent();
    List<Part> parts = Objects.requireNonNull(content).getParts();
    Part first = Objects.requireNonNull(parts).getFirst();
    return first.getText();
  }
}