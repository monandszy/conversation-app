package code.modules.conversation.service;

import static code.modules.conversation.service.domain.Conversation.ConversationId;
import static code.modules.conversation.service.domain.Request.RequestId;
import static code.modules.conversation.service.domain.Response.ResponseId;
import static code.modules.conversation.service.domain.Section.SectionId;

import code.modules.conversation.IConversationCommandFacade;
import code.modules.conversation.IConversationQueryFacade.ConversationReadDto;
import code.modules.conversation.IConversationQueryFacade.RequestReadDto;
import code.modules.conversation.IConversationQueryFacade.ResponseReadDto;
import code.modules.conversation.IConversationQueryFacade.SectionReadDto;
import code.modules.conversation.service.domain.AccountId;
import code.modules.conversation.service.domain.Conversation;
import code.modules.conversation.service.domain.Request;
import code.modules.conversation.service.domain.Response;
import code.modules.conversation.service.domain.Section;
import code.modules.conversation.util.ConversationMapper;
import code.modules.google_api.GoogleApiAdapter;
import code.modules.google_api.GoogleApiAdapter.ApiRequestDto;
import code.modules.google_api.GoogleApiAdapter.ApiResponseDto;
import code.util.Facade;
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

  @Override
  public ConversationReadDto begin(UUID accountId) {
    Conversation conversation = Conversation.builder()
      .accountId(new AccountId(accountId))
      .created(OffsetDateTime.now())
      .id(ConversationId.generate())
      .build();
    conversation = commandDao.create(conversation);
    return mapper.domainToReadDto(conversation);
  }

  @Override
  public SectionReadDto generate(
    GenerateDto generateDto,
    UUID conversationId
  ) {
    ApiRequestDto apiRequest = new ApiRequestDto(generateDto.text());
    ApiResponseDto apiResponse = googleApiAdapter.generate(apiRequest);

    OffsetDateTime now = OffsetDateTime.now();
    Conversation dependency = Conversation.builder().id(new ConversationId(conversationId)).build();
    Section section = Section.builder()
      .id(SectionId.generate())
      .conversation(dependency).created(now).build();
    Request request = Request.builder()
      .id(RequestId.generate())
      .text(apiRequest.text()).selected(true).created(now).build();
    Response response = Response.builder()
      .id(ResponseId.generate())
      .text(apiResponse.text()).selected(true).created(now).build();
    section = commandDao.create(section, request, response);
    return mapper.domainToReadDto(section);
  }

  @Override
  public RequestReadDto regenerate(
    GenerateDto generateDto,
    UUID sectionId
  ) {
    ApiRequestDto apiRequest = new ApiRequestDto(generateDto.text());
    ApiResponseDto apiResponse = googleApiAdapter.generate(apiRequest);
    OffsetDateTime now = OffsetDateTime.now();

    Section dependency = Section.builder().id(new SectionId(sectionId)).build();
    Request request = Request.builder().section(dependency)
      .id(RequestId.generate())
      .text(apiRequest.text()).selected(true).created(now).build();
    Response response = Response.builder().text(apiResponse.text())
      .id(ResponseId.generate())
      .selected(true).created(now).build();
    request = commandDao.create(request, response);
    return mapper.domainToReadDto(request);
  }

  @Override
  public ResponseReadDto retry(UUID requestId) {
    Request dependency = readDao.getRequest(new RequestId(requestId));
    ApiRequestDto apiRequest = new ApiRequestDto(dependency.getText());
    ApiResponseDto apiResponse = googleApiAdapter.generate(apiRequest);
    OffsetDateTime now = OffsetDateTime.now();
    Response response = Response.builder().request(dependency)
      .id(new ResponseId(UUID.randomUUID()))
      .text(apiResponse.text()).selected(true).created(now).build();
    response = commandDao.create(response);
    return mapper.domainToReadDto(response);
  }

  @Override
  public void deleteConversation(UUID conversationId) {
    commandDao.deleteConversation(new ConversationId(conversationId));
  }

  @Override
  public void deleteSection(UUID sectionId) {
    commandDao.deleteSection(new SectionId(sectionId));
  }

  @Override
  public void deleteRequest(UUID requestId) {
    commandDao.deleteRequest(new RequestId(requestId));
  }

  @Override
  public void deleteResponse(UUID responseId) {
    commandDao.deleteResponse(new ResponseId(responseId));
  }
}