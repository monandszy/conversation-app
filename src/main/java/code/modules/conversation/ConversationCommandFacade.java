package code.modules.conversation;

import code.modules.conversation.ConversationQueryFacade.ConversationReadDto;
import code.modules.conversation.ConversationQueryFacade.RequestReadDto;
import code.modules.conversation.ConversationQueryFacade.ResponseReadDto;
import code.modules.conversation.ConversationQueryFacade.SectionReadDto;
import code.modules.conversation.service.CommandConversationDao;
import code.modules.conversation.service.Conversation;
import code.modules.conversation.service.ReadConversationDao;
import code.modules.conversation.service.Request;
import code.modules.conversation.service.Response;
import code.modules.conversation.service.Section;
import code.modules.conversation.util.ConversationMapper;
import code.modules.googleApi.GoogleApiAdapter;
import code.modules.googleApi.GoogleApiAdapter.ApiRequestDto;
import code.modules.googleApi.GoogleApiAdapter.ApiResponseDto;
import code.util.Facade;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.With;

@Facade
@AllArgsConstructor
public class ConversationCommandFacade {

  private GoogleApiAdapter googleApiAdapter;
  private ConversationMapper mapper;
  private CommandConversationDao commandDao;
  private ReadConversationDao readDao;

  public ConversationReadDto begin(@Valid ConversationBeginDto conversationDto) {
    Conversation conversation = mapper.createDtoToDomain(conversationDto);
    conversation = commandDao.create(conversation.withCreated(OffsetDateTime.now()));
    return mapper.domainToReadDto(conversation);
  }

  // creates conversationItem, persists request and response
  public SectionReadDto generate(@Valid RequestGenerateDto generateDto, UUID conversationId) {
    // call to other module facade, a nested dependency
    ApiRequestDto apiRequest = new ApiRequestDto(generateDto.text());
    ApiResponseDto apiResponse = googleApiAdapter.generate(apiRequest);

    OffsetDateTime now = OffsetDateTime.now();
    Conversation dependency = Conversation.builder().id(conversationId).build();
    Section section = Section.builder().conversation(dependency).created(now).build();
    Request request = Request.builder().text(apiRequest.text()).selected(true).created(now).build();
    Response response = Response.builder().text(apiResponse.text()).selected(true).created(now).build();
    section = commandDao.create(section, request, response);
    return mapper.domainToReadDto(section);
  }

  public RequestReadDto regenerate(@Valid RequestGenerateDto generateDto, UUID sectionId) {
    ApiRequestDto apiRequest = new ApiRequestDto(generateDto.text());
    ApiResponseDto apiResponse = googleApiAdapter.generate(apiRequest);
    OffsetDateTime now = OffsetDateTime.now();

    Section dependency = Section.builder().id(sectionId).build();
    Request request = Request.builder().section(dependency).text(apiRequest.text()).selected(true).created(now).build();
    Response response = Response.builder().text(apiResponse.text()).selected(true).created(now).build();
    request = commandDao.create(request, response);
    return mapper.domainToReadDto(request);
  }

  public ResponseReadDto retry(UUID requestId) {
    Request dependency = readDao.getRequest(requestId);
    ApiRequestDto apiRequest = new ApiRequestDto(dependency.getText());
    ApiResponseDto apiResponse = googleApiAdapter.generate(apiRequest);
    OffsetDateTime now = OffsetDateTime.now();
    Response response = Response.builder().request(dependency).text(apiResponse.text()).selected(true).created(now)
      .build();
    response = commandDao.create(response);
    return mapper.domainToReadDto(response);
  }

  public void deleteConversation(UUID conversationId, UUID accountId) {
    commandDao.deleteConversation(conversationId, accountId);
  }

  public void deleteSection(UUID sectionId, UUID accountId) {
    commandDao.deleteSection(sectionId, accountId);
  }

  public void deleteRequest(UUID requestId, UUID accountId) {
    commandDao.deleteRequest(requestId, accountId);
  }

  public void deleteResponse(UUID responseId, UUID accountId) {
    commandDao.deleteResponse(responseId, accountId);
  }

  public record ConversationBeginDto(
    @NotNull
    UUID accountId
  ) {}

  @With
  public record RequestGenerateDto(
    @NotBlank
    String text
  ) {}
}