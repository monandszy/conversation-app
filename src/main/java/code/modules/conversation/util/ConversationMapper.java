package code.modules.conversation.util;

import static code.modules.conversation.IConversationQueryFacade.RequestReadDto;
import static code.modules.conversation.IConversationQueryFacade.SectionReadDto;

import code.configuration.SpringMapperConfig;
import code.modules.conversation.IConversationCommandFacade.ConversationBeginDto;
import code.modules.conversation.IConversationQueryFacade.ConversationReadDto;
import code.modules.conversation.IConversationQueryFacade.ResponseReadDto;
import code.modules.conversation.data.entity.ConversationEntity;
import code.modules.conversation.data.entity.RequestEntity;
import code.modules.conversation.data.entity.ResponseEntity;
import code.modules.conversation.data.entity.SectionEntity;
import code.modules.conversation.data.jpa.projection.RequestWindow;
import code.modules.conversation.data.jpa.projection.ResponseWindow;
import code.modules.conversation.data.jpa.projection.SectionWindow;
import code.modules.conversation.service.domain.Conversation;
import code.modules.conversation.service.domain.Navigation;
import code.modules.conversation.service.domain.Request;
import code.modules.conversation.service.domain.Response;
import code.modules.conversation.service.domain.Section;
import code.util.Generated;
import java.util.List;
import java.util.Objects;
import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = SpringMapperConfig.class)
@AnnotateWith(Generated.class)
public interface ConversationMapper {

  ConversationReadDto domainToReadDto(Conversation domain);

  SectionReadDto domainToReadDto(Section domain);

  ResponseReadDto domainToReadDto(Response response);

  RequestReadDto domainToReadDto(Request request);

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

  ConversationReadDto entityToDomain(Conversation conversation);

  default Section entityToDomain(SectionWindow entityDto) {
    SectionEntity section = entityDto.section();
    if (Objects.isNull(section)) {
      return null;
    }

    RequestEntity selectedRequest = entityDto.selectedRequest();
    ResponseEntity selectedResponse = entityDto.selectedResponse();

    Navigation nextRequestNav = Navigation.builder()
      .nextId(entityDto.nextRequestId())
      .previousId(entityDto.prevRequestId())
      .build();

    Navigation nextResponseNav = Navigation.builder()
      .nextId(entityDto.nextResponseId())
      .previousId(entityDto.prevResponseId())
      .build();

    return entityToDomain(section)
      .withRequests(selectedRequest == null
        ? List.of() : List.of(entityToDomain(selectedRequest)
        .withNavigation(nextRequestNav)
        .withResponses(selectedResponse == null
          ? List.of() : List.of(entityToDomain(selectedResponse)
          .withNavigation(nextResponseNav))
        ))
      );
  }

  default Request entityToDomain(RequestWindow entityDto) {
    RequestEntity selectedRequest = entityDto.selectedRequest();
    ResponseEntity selectedResponse = entityDto.selectedResponse();

    if (Objects.isNull(selectedRequest)) {
      return null;
    }

    Navigation nextRequestNav = Navigation.builder()
      .nextId(entityDto.nextRequestId())
      .previousId(entityDto.prevRequestId())
      .build();

    Navigation nextResponseNav = Navigation.builder()
      .nextId(entityDto.nextResponseId())
      .previousId(entityDto.prevResponseId())
      .build();

    return entityToDomain(selectedRequest)
      .withNavigation(nextRequestNav)
      .withResponses(selectedResponse == null
        ? List.of() : List.of(entityToDomain(selectedResponse)
        .withNavigation(nextResponseNav)
      ));
  }

  default Response entityToDomain(ResponseWindow entityDto) {
    ResponseEntity selectedResponse = entityDto.selectedResponse();

    if (Objects.isNull(selectedResponse)) {
      return null;
    }

    Navigation nextResponseNav = Navigation.builder()
      .nextId(entityDto.nextResponseId())
      .previousId(entityDto.prevResponseId())
      .build();

    return entityToDomain(selectedResponse)
      .withNavigation(nextResponseNav);
  }

}