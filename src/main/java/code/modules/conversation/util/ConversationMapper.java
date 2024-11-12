package code.modules.conversation.util;

import code.configuration.SpringMapperConfig;
import code.modules.conversation.ConversationCommandFacade.ConversationBeginDto;
import code.modules.conversation.ConversationQueryFacade.ConversationReadDto;
import static code.modules.conversation.ConversationQueryFacade.RequestReadDto;
import code.modules.conversation.ConversationQueryFacade.ResponseReadDto;
import static code.modules.conversation.ConversationQueryFacade.SectionReadDto;
import code.modules.conversation.data.ConversationEntity;
import code.modules.conversation.data.RequestEntity;
import code.modules.conversation.data.ResponseEntity;
import code.modules.conversation.data.SectionEntity;
import code.modules.conversation.data.jpa.RequestNavigationProjection;
import code.modules.conversation.data.jpa.ResponseNavigationProjection;
import code.modules.conversation.data.jpa.SectionNavigationProjection;
import code.modules.conversation.service.Conversation;
import code.modules.conversation.service.Navigation;
import code.modules.conversation.service.Request;
import code.modules.conversation.service.Response;
import code.modules.conversation.service.Section;
import code.util.Generated;
import java.util.List;
import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = SpringMapperConfig.class)
@AnnotateWith(Generated.class)
public interface ConversationMapper {

  ConversationReadDto domainToReadDto(Conversation domain);

  SectionReadDto domainToReadDto(Section domain);

  Conversation createDtoToDomain(ConversationBeginDto domain);

  ConversationEntity domainToEntity(Conversation domain);

  SectionEntity domainToEntity(Section domain);

  RequestEntity domainToEntity(Request domain);

  ResponseEntity domainToEntity(Response domain);

  @Mapping(target = "conversation", source = "conversation", ignore = true)
  Section entityToDomain(SectionEntity entity);

  @Mapping(target = "section", source = "section", ignore = true)
  Request entityToDomain(RequestEntity entity);

  @Mapping(target = "request", source = "request", ignore = true)
  Response entityToDomain(ResponseEntity entity);

  Conversation entityToDomain(ConversationEntity entity);

  ResponseReadDto domainToReadDto(Response response);

  RequestReadDto domainToReadDto(Request request);

  default Section entityToDomain(SectionNavigationProjection entityDto) {
    SectionEntity section = entityDto.getSection();
    if (section == null) {
      return null;
    }

    RequestEntity selectedRequest = entityDto.getSelectedRequest();
    ResponseEntity selectedResponse = entityDto.getSelectedResponse();

    Navigation nextRequestNav = Navigation.builder()
      .nextId(entityDto.getNextRequestId())
      .previousId(entityDto.getPrevRequestId())
      .build();

    Navigation nextResponseNav = Navigation.builder()
      .nextId(entityDto.getNextResponseId())
      .previousId(entityDto.getPrevResponseId())
      .build();

    return entityToDomain(section)
      .withRequests(selectedRequest == null ? List.of() : List.of(entityToDomain(selectedRequest)
        .withNavigation(nextRequestNav)
        .withResponses(selectedResponse == null ? List.of() : List.of(entityToDomain(selectedResponse)
          .withNavigation(nextResponseNav))
        ))
      );
  }

  default Request entityToDomain(RequestNavigationProjection entityDto) {
    RequestEntity selectedRequest = entityDto.getSelectedRequest();
    ResponseEntity selectedResponse = entityDto.getSelectedResponse();

    if (selectedRequest == null) {
      return null;
    }

    Navigation nextRequestNav = Navigation.builder()
      .nextId(entityDto.getNextRequestId())
      .previousId(entityDto.getPrevRequestId())
      .build();

    Navigation nextResponseNav = Navigation.builder()
      .nextId(entityDto.getNextResponseId())
      .previousId(entityDto.getPrevResponseId())
      .build();

    return entityToDomain(selectedRequest)
      .withNavigation(nextRequestNav)
      .withResponses(selectedResponse == null ? List.of() : List.of(entityToDomain(selectedResponse)
        .withNavigation(nextResponseNav)
      ));
  }

  default Response entityToDomain(ResponseNavigationProjection entityDto) {
    ResponseEntity selectedResponse = entityDto.getSelectedResponse();

    if (selectedResponse == null) {
      return null;
    }

    Navigation nextResponseNav = Navigation.builder()
      .nextId(entityDto.getNextResponseId())
      .previousId(entityDto.getPrevResponseId())
      .build();

    return entityToDomain(selectedResponse)
      .withNavigation(nextResponseNav);
  }

}