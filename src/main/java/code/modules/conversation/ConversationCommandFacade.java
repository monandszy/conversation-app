package code.modules.conversation;

import code.modules.conversation.ConversationQueryFacade.ConversationReadDto;
import code.modules.conversation.ConversationQueryFacade.SectionReadDto;
import code.modules.conversation.service.Conversation;
import code.modules.conversation.service.ConversationDao;
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
  private ConversationDao conversationDao;

  // creates conversationItem, persists request and response
  public SectionReadDto generate(@Valid RequestGenerateDto requestDto) {
    // call to other module facade, a nested dependency
    ApiRequestDto apiRequest = new ApiRequestDto(requestDto.text());
    ApiResponseDto apiResponse = googleApiAdapter.generate(apiRequest);

    OffsetDateTime now = OffsetDateTime.now();
    Conversation dependency = Conversation.builder().id(requestDto.dependencyId()).build();
    Response response = Response.builder().text(apiResponse.text()).selected(true).created(now)
      .request(Request.builder().text(apiRequest.text()).selected(true).created(now)
        .section(Section.builder().conversation(dependency).created(now)
          .build()).build()).build();
    Section section = conversationDao.create(response);
    return mapper.domainToReadDto(section);
  }

  public void regenerate(RequestGenerateDto generateDto) {
    // dependencyId = section
  }

  public void retry(RequestGenerateDto generateDto) {
    // dependencyId = request
  }

  public ConversationReadDto begin(@Valid ConversationBeginDto conversationDto) {
    Conversation conversation = mapper.createDtoToDomain(conversationDto);
    conversation = conversationDao.create(conversation.withCreated(OffsetDateTime.now()));
    return mapper.domainToReadDto(conversation);
  }

  public record ConversationBeginDto(
    @NotNull
    UUID accountId
  ) {}

  @With
  public record RequestGenerateDto(
    @NotBlank
    String text,
    UUID dependencyId
  ) {}
}