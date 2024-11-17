package code.modules.google_api.internal;

import code.openApi.ApiClient;
import code.openApi.infrastructure.CachedContentsApi;
import code.openApi.infrastructure.CorporaApi;
import code.openApi.infrastructure.FilesApi;
import code.openApi.infrastructure.GeneratedFilesApi;
import code.openApi.infrastructure.MediaApi;
import code.openApi.infrastructure.ModelsApi;
import code.openApi.model.CachedContent;
import code.openApi.model.Content;
import code.openApi.model.CreateFileRequest;
import code.openApi.model.CreateFileResponse;
import code.openApi.model.GenerateContentRequest;
import code.openApi.model.GenerateContentResponse;
import code.openApi.model.ListFilesResponse;
import code.openApi.model.ListModelsResponse;
import code.openApi.model.ModelFile;
import code.openApi.model.Part;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class ApiCallService {

  private ModelsApi modelsApi;

  private static final String errorFormat = "1";
  private static final String dataFormat = "json";
  private static final String apiToken = Authorization.GoogleApiToken;
  private static final String model = "gemini-1.5-flash";
  // String | Selector specifying which fields to include in a partial response.
  private static final String fields = null; // I have no idea how to use this
  private static final Boolean isPrettyPrint = false;
  private static final String userIdentificationQuota = "TEST_USER";

// String | Upload protocol for media (e.g. \"raw\", \"multipart\").
//    String uploadProtocol = "uploadProtocol_example";
// String | Legacy upload protocol for media (e.g. \"media\", \"multipart\").
//    String uploadType = "uploadType_example";

  public static void main(String[] args) {
    ApiCallService apiCallService = new ApiCallService(new ModelsApi());
    apiCallService.attempt();
  }

  public void attempt() {
    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath("https://generativelanguage.googleapis.com");
    FilesApi filesApi = new FilesApi(apiClient);
    GeneratedFilesApi generatedFilesApi = new GeneratedFilesApi(apiClient);
    MediaApi mediaApi = new MediaApi(apiClient);
    CachedContentsApi cachedContentsApi = new CachedContentsApi(apiClient);
    CorporaApi corporaApi = new CorporaApi(apiClient);

  }

  private void attemptCommunication(CachedContentsApi cachedContentsApi) {
    CachedContent block = cachedContentsApi.generativelanguageCachedContentsGet(
      "", null, null, null, null, null, apiToken, null, null, null, null, null
    ).block();
    System.out.println(block);
  }

  private void addToCache(CachedContentsApi cachedContentsApi) {
    GenerateContentRequest request = new GenerateContentRequest();
    Part part = new Part();
    part.setText("QUERY");

    Content content = new Content();
    content.setParts(List.of(part));
    request.setContents(List.of(content));
    GenerateContentResponse generateContentResponse = modelsGenerateContent(request);
    Content recivedContent = generateContentResponse.getCandidates().getFirst().getContent();

    CachedContent cachedContent = new CachedContent();
    cachedContent.addContentsItem(recivedContent);
    Mono<CachedContent> raw = cachedContentsApi.generativelanguageCachedContentsCreate(
      errorFormat,
      null,
      dataFormat,
      null, null, apiToken,
      null, isPrettyPrint,
      userIdentificationQuota, null, null, cachedContent
    );
    CachedContent block = raw.block();
    System.out.println(block);
  }

  private static void createFile(MediaApi mediaApi) {
    CreateFileRequest createFileRequest = new CreateFileRequest();
    ModelFile file = new ModelFile(null, null, null, null, null, null, null, "./lombok.config");
    createFileRequest._file(file);
    createFileRequest.setFile(file);
    Mono<CreateFileResponse> createFileResponseMono = mediaApi.generativelanguageMediaUpload(
      errorFormat,
      null,
      dataFormat,
      null, null, apiToken,
      null, isPrettyPrint,
      userIdentificationQuota, "raw", null, createFileRequest
    );
    CreateFileResponse block = createFileResponseMono.block();
    System.out.println(block);
  }

  private static void getFileList(FilesApi filesApi) {
    ListFilesResponse block = filesApi.generativelanguageFilesList(
      errorFormat,
      null,
      dataFormat,
      null, null, apiToken,
      null, isPrettyPrint,
      null, null, null, null, null
    ).block();
    System.out.println(block);
  }

  public GenerateContentResponse modelsGenerateContent(GenerateContentRequest request) {
    return modelsApi.generativelanguageModelsGenerateContent(
      model,
      errorFormat,
      null,
      dataFormat,
      null,
      fields,
      apiToken,
      null,
      isPrettyPrint,
      userIdentificationQuota,
      null,
      null,
      request
    ).block();
  }

  public ListModelsResponse modelsList() {
    return modelsApi.generativelanguageModelsList(
      errorFormat,
      null,
      dataFormat,
      null,
      null,
      apiToken,
      null,
      null,
      null,
      null,
      null,
      null,
      null
    ).block();
  }
}