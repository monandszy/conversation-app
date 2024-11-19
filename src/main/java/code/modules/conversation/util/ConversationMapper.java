package code.modules.conversation.util;

import static code.modules.conversation.IConversationQueryFacade.RequestReadDto;
import static code.modules.conversation.IConversationQueryFacade.SectionReadDto;

import code.configuration.Constants;
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
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

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


  default Section projectionToDomain(Object[] projection) {
    Request request = requestProjectionToDomain(projection);
    UUID sectionId = (UUID) projection[10];
    OffsetDateTime sectionCreated = (OffsetDateTime) projection[11];
    return Section.builder()
      .requests(List.of(request))
      .id(new Section.SectionId(sectionId))
      .created(sectionCreated)
      .build();
  }

  default Request requestProjectionToDomain(Object[] projection) {
    Response response = responseProjectionToDomain(projection);
    UUID requestId = (UUID) projection[5];
    OffsetDateTime requestCreated = (OffsetDateTime) projection[6];
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
      .created(requestCreated)
      .text(requestText)
      .navigation(requestNav)
      .build();
  }

  default Response responseProjectionToDomain(Object[] projection) {
    UUID responseId = (UUID) projection[0];
    OffsetDateTime responseCreated = (OffsetDateTime) projection[1];
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
      .created(responseCreated)
      .navigation(responseNav)
      .build();
  }

  default Page<Conversation> conversationProjectionToDomain(Object[] projection) {
    Integer pageNumber = (Integer) projection[0];
    Long totalAmount = (Long) projection[1];
    String[] concatenatedDataArray = (String[]) projection[2];
    List<Conversation> content = Arrays.stream(concatenatedDataArray)
      .map(concatenated -> {
        String[] parts = concatenated.split(","); // Split by comma
        UUID id = UUID.fromString(parts[0]);
        OffsetDateTime created = OffsetDateTime.parse(parts[1], DB_FORMATTER);
        return Conversation.builder()
          .id(new Conversation.ConversationId(id))
          .created(created)
          .build();
      }).toList();
    PageRequest pageRequest = PageRequest.of(pageNumber, Constants.PAGE_SIZE, Sort.by("created").descending());
    return new PageImpl<>(content, pageRequest, totalAmount);
  }

  DateTimeFormatter DB_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSX");
}