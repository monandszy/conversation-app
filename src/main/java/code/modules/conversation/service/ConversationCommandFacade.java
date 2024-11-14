package code.modules.conversation.service;

import code.modules.conversation.IConversationCommandFacade;
import code.modules.conversation.IConversationQueryFacade.ConversationReadDto;
import code.modules.conversation.IConversationQueryFacade.RequestReadDto;
import code.modules.conversation.IConversationQueryFacade.ResponseReadDto;
import code.modules.conversation.IConversationQueryFacade.SectionReadDto;
import code.modules.conversation.service.domain.Conversation;
import code.modules.conversation.service.domain.Request;
import code.modules.conversation.service.domain.Response;
import code.modules.conversation.service.domain.Section;
import code.modules.conversation.util.ConversationMapper;
import code.modules.googleApi.GoogleApiAdapter;
import code.modules.googleApi.GoogleApiAdapter.ApiRequestDto;
import code.modules.googleApi.GoogleApiAdapter.ApiResponseDto;
import code.util.Facade;
import jakarta.validation.Valid;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;

@Facade
@AllArgsConstructor
public class ConversationCommandFacade implements IConversationCommandFacade {

  private GoogleApiAdapter googleApiAdapter;
  private ConversationMapper mapper;
  private CommandConversationDao commandDao;
  private ReadConversationDao readDao;

  public ConversationReadDto begin(@Valid ConversationBeginDto conversationDto) {
    Conversation conversation = mapper.createDtoToDomain(conversationDto);
    conversation = commandDao.create(conversation.withCreated(OffsetDateTime.now()));
    return mapper.domainToReadDto(conversation);
  }

  public SectionReadDto generate(@Valid RequestGenerateDto generateDto, UUID conversationId) {
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
    Request request = Request.builder().section(dependency)
      .text(apiRequest.text()).selected(true).created(now).build();
    Response response = Response.builder().text(apiResponse.text())
      .selected(true).created(now).build();
    request = commandDao.create(request, response);
    return mapper.domainToReadDto(request);
  }

  public ResponseReadDto retry(UUID requestId) {
    Request request = Request.builder().id(requestId).build();
    Request dependency = readDao.getRequest(request);
    ApiRequestDto apiRequest = new ApiRequestDto(dependency.getText());
    ApiResponseDto apiResponse = googleApiAdapter.generate(apiRequest);
    OffsetDateTime now = OffsetDateTime.now();
    Response response = Response.builder().request(dependency)
      .text(apiResponse.text()).selected(true).created(now).build();
    response = commandDao.create(response);
    return mapper.domainToReadDto(response);
  }

  public void deleteConversation(UUID conversationId, UUID accountId) {
    Conversation conversation = Conversation.builder().id(conversationId).build();
    commandDao.deleteConversation(conversation, accountId);
  }

  public void deleteSection(UUID sectionId, UUID accountId) {
    Section section = Section.builder().id(sectionId).build();
    commandDao.deleteSection(section, accountId);
  }

  public void deleteRequest(UUID requestId, UUID accountId) {
    Request request = Request.builder().id(requestId).build();
    commandDao.deleteRequest(request, accountId);
  }

  public void deleteResponse(UUID responseId, UUID accountId) {
    Response response = Response.builder().id(responseId).build();
    commandDao.deleteResponse(response, accountId);
  }
}