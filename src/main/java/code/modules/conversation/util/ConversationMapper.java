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
import code.modules.conversation.service.domain.Conversation;
import code.modules.conversation.service.domain.Request;
import code.modules.conversation.service.domain.Request.RequestId;
import code.modules.conversation.service.domain.Response;
import code.modules.conversation.service.domain.Response.ResponseId;
import code.modules.conversation.service.domain.Section;
import code.util.Generated;
import java.time.OffsetDateTime;
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
  @Mapping(source = "navigation.responseCount", target = "navigation.count")
  @Mapping(source = "navigation.responsePosition", target = "navigation.position")
  @Mapping(source = "navigation.nextId.value", target = "navigation.nextId")
  @Mapping(source = "navigation.previousId.value", target = "navigation.previousId")
  ResponseReadDto domainToReadDto(Response response);

  @Mapping(source = "id.value", target = "id")
  @Mapping(source = "navigation.requestCount", target = "navigation.count")
  @Mapping(source = "navigation.requestPosition", target = "navigation.position")
  @Mapping(source = "navigation.nextId.value", target = "navigation.nextId")
  @Mapping(source = "navigation.previousId.value", target = "navigation.previousId")
  RequestReadDto domainToReadDto(Request request);

  ConversationEntity domainToEntity(Conversation domain);

  SectionEntity domainToEntity(Section domain);

  RequestEntity domainToEntity(Request domain);

  ResponseEntity domainToEntity(Response domain);

  @Mapping(target = "section", source = "section", ignore = true)
  Request entityToDomain(RequestEntity entity);

  @Mapping(target = "request", source = "request", ignore = true)
  Response entityToDomain(ResponseEntity entity);

  Conversation entityToDomain(ConversationEntity entity);


  default Section sectionProjectionToDomain(Object[] projection) {
    Request request = requestProjectionToDomain(projection);
    UUID sectionId = (UUID) projection[14];
    OffsetDateTime sectionCreated = OffsetDateTime.parse((String) projection[15]);
    return Section.builder()
      .requests(List.of(request))
      .id(new Section.SectionId(sectionId))
      .created(sectionCreated)
      .build();
  }

  default Request requestProjectionToDomain(Object[] projection) {
    Response response = responseProjectionToDomain(projection);
    UUID requestId = (UUID) projection[7];
    OffsetDateTime requestCreated = OffsetDateTime.parse((String) projection[8]);
    String requestText = (String) projection[9];
    UUID prevRequestId = (UUID) projection[10];
    UUID nextRequestId = (UUID) projection[11];
    Long requestCount = (Long) projection[12];
    Long requestPosition = (Long) projection[13];
    Request.RequestNavigation requestNav = new Request.RequestNavigation(
      requestCount,
      requestPosition,
      new RequestId(nextRequestId),
      new RequestId(prevRequestId)
    );
    return Request.builder()
      .responses(List.of(response))
      .id(new RequestId(requestId))
      .created(requestCreated)
      .text(requestText)
      .navigation(requestNav)
      .build();
  }

  default Response responseProjectionToDomain(Object[] projection) {
    UUID responseId = (UUID) projection[0];
    OffsetDateTime responseCreated = OffsetDateTime.parse((String) projection[1]);
    String responseText = (String) projection[2];
    UUID prevResponseId = (UUID) projection[3];
    UUID nextResponseId = (UUID) projection[4];
    Long responseCount = (Long) projection[5];
    Long responsePosition = (Long) projection[6];
    Response.ResponseNavigation responseNav = new Response.ResponseNavigation(
      responseCount,
      responsePosition,
      new ResponseId(nextResponseId),
      new ResponseId(prevResponseId)
    );
    return Response.builder()
      .id(new ResponseId(responseId))
      .text(responseText)
      .created(responseCreated)
      .navigation(responseNav)
      .build();
  }
}