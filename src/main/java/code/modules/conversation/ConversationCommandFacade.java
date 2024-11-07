package code.modules.conversation;

import code.modules.conversation.ConversationQueryFacade.ConversationReadDto;
import code.modules.conversation.ConversationQueryFacade.RequestReadDto;
import code.modules.conversation.service.Conversation;
import code.modules.conversation.service.ConversationDao;
import code.modules.conversation.service.Request;
import code.modules.conversation.service.Response;
import code.modules.conversation.util.ConversationMapper;
import code.modules.googleApi.GoogleApiAdapter;
import code.modules.googleApi.GoogleApiAdapter.ApiRequestDto;
import code.modules.googleApi.GoogleApiAdapter.ApiResponseDto;
import code.util.Facade;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.With;

@Facade
@AllArgsConstructor
public class ConversationCommandFacade {

  private GoogleApiAdapter googleApiAdapter;
  private ConversationMapper mapper;
  private ConversationDao conversationDao;

  public RequestReadDto generate(@Valid RequestGenerateDto requestDto, UUID conversationId) {
    // call to other module facade, a nested dependency
    ApiRequestDto apiRequest = new ApiRequestDto(requestDto.text());
    ApiResponseDto apiResponse = googleApiAdapter.generate(apiRequest);

    Conversation conversation = Conversation.builder().id(conversationId).build();
    Request request = mapper.createDtoToDomain(requestDto);
    Response response = Response.builder()
      .text(apiResponse.text()).request(request).build();
    request = conversationDao.create(request
      .withConversation(conversation)
      .withResponses(List.of(response))
    );
    return mapper.domainToReadDto(request);
  }

  public ConversationReadDto begin(ConversationBeginDto conversationDto) {
    Conversation conversation = mapper.createDtoToDomain(conversationDto);
    conversation = conversationDao.create(conversation);
    return mapper.domainToReadDto(conversation);
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