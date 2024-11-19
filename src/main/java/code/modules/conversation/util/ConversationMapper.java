package code.modules.conversation.util;

import static code.modules.conversation.IConversationQueryFacade.RequestReadDto;
import static code.modules.conversation.IConversationQueryFacade.SectionReadDto;

import code.configuration.SpringMapperConfig;
import code.modules.conversation.IConversationQueryFacade.ConversationReadDto;
import code.modules.conversation.IConversationQueryFacade.ResponseReadDto;
import code.modules.conversation.data.entity.ConversationEntity;
import code.modules.conversation.data.entity.RequestEntity;
import code.modules.conversation.data.entity.ResponseEntity;
import code.modules.conversation.data.entity.SectionEntity;
import code.modules.conversation.data.jpa.projection.SectionWindow;
import code.modules.conversation.service.domain.Conversation;
import code.modules.conversation.service.domain.Request;
import code.modules.conversation.service.domain.Request.RequestId;
import code.modules.conversation.service.domain.Response;
import code.modules.conversation.service.domain.Response.ResponseId;
import code.modules.conversation.service.domain.Section;
import code.util.Generated;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = SpringMapperConfig.class)
@AnnotateWith(Generated.class)
public interface ConversationMapper {

  @Mapping(source = "id.value", target = "id")
  ConversationReadDto domainToReadDto(Conversation domain);

  @Mapping(source = "id.value", target = "id")
  SectionReadDto domainToReadDto(Section domain);

  @Mapping(source = "id.value", target = "id")
  @Mapping(source = "navigation.nextId.value", target = "navigation.nextId")
  @Mapping(source = "navigation.previousId.value", target = "navigation.previousId")
  ResponseReadDto domainToReadDto(Response response);

  @Mapping(source = "id.value", target = "id")
  @Mapping(source = "navigation.nextId.value", target = "navigation.nextId")
  @Mapping(source = "navigation.previousId.value", target = "navigation.previousId")
  RequestReadDto domainToReadDto(Request request);

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

  default Section entityToDomain(SectionWindow entityDto) {
    SectionEntity section = entityDto.section();
    if (section == null) {
      return null;
    }

    RequestEntity selectedRequest = entityDto.selectedRequest();
    ResponseEntity selectedResponse = entityDto.selectedResponse();

    Request.RequestNavigation requestNav = new Request.RequestNavigation(
      entityDto.nextRequestId(),
      entityDto.prevRequestId()
    );

    Response.ResponseNavigation responseNav = new Response.ResponseNavigation(
      entityDto.nextResponseId(),
      entityDto.prevResponseId()
    );

    return entityToDomain(section)
      .withRequests(selectedRequest == null
        ? List.of() : List.of(entityToDomain(selectedRequest)
        .withNavigation(requestNav)
        .withResponses(selectedResponse == null
          ? List.of() : List.of(entityToDomain(selectedResponse)
          .withNavigation(responseNav))
        ))
      );
  }

  default Request requestProjectionToDomain(Object[] projection) {
    Response response = responseProjectionToDomain(projection);
    UUID requestId = (UUID) projection[5];
    Instant requestCreated = (Instant) projection[6];
    String requestText = (String) projection[7];
    UUID prevRequestId = (UUID) projection[8];
    UUID nextRequestId = (UUID) projection[9];
    Request.RequestNavigation requestNav = new Request.RequestNavigation(
      new RequestId(nextRequestId),
      new RequestId(prevRequestId)
    );
    return Request.builder()
      .responses(List.of(response))
      .id(new RequestId(requestId))
      .created(OffsetDateTime.ofInstant(requestCreated, ZoneId.systemDefault()))
      .text(requestText)
      .navigation(requestNav)
      .build();
  }

  default Response responseProjectionToDomain(Object[] projection) {
    UUID responseId = (UUID) projection[0];
    Instant responseCreated = (Instant) projection[1];
    String responseText = (String) projection[2];
    UUID prevResponseId = (UUID) projection[3];
    UUID nextResponseId = (UUID) projection[4];
    Response.ResponseNavigation responseNav = new Response.ResponseNavigation(
      new ResponseId(nextResponseId),
      new ResponseId(prevResponseId)
    );
    return Response.builder()
      .id(new ResponseId(responseId))
      .text(responseText)
      .created(OffsetDateTime.ofInstant(responseCreated, ZoneId.systemDefault()))
      .navigation(responseNav)
      .build();
  }

}