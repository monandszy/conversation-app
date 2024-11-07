package code.modules.googleApi;

import code.modules.googleApi.internal.ApiCallService;
import code.modules.googleApi.internal.ApiModelMapper;
import code.openApi.model.GenerateContentResponse;
import code.openApi.model.ListModelsResponse;
import code.openApi.model.Model;
import code.util.Facade;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.AllArgsConstructor;

@Facade
@AllArgsConstructor
public class GoogleApiAdapter {

  private ApiModelMapper apiModelMapper;
  private ApiCallService apiCallService;
  private ObjectMapper objectMapper;

  public ApiResponseDto generate(ApiRequestDto request) {
    try {
      GenerateContentResponse response = apiCallService.modelsGenerateContent(apiModelMapper.dtoToApiModel(request));
      return apiModelMapper.apiModelToDto(response);
    } catch (Exception e) {
      throw new InternalApiException(e.getMessage(), e.getCause());
    }
  }

  public List<String> getModelList() {
    try {
      ListModelsResponse response = apiCallService.modelsList();
      List<Model> models = response.getModels();
      return models.stream().map(Model::getName).toList();
    } catch (Exception e) {
      throw new InternalApiException(e.getMessage(), e.getCause());
    }
  }

  public record ApiRequestDto(
    @NotBlank
    String text
  ) {}

  public record ApiResponseDto(
    String text
  ) {}
}