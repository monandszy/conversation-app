package code.modules.conversation.util;

import code.configuration.SpringMapperConfig;
import code.modules.conversation.ConversationCommandFacade;
import code.modules.conversation.ConversationCommandFacade.ConversationBeginDto;
import code.modules.conversation.ConversationQueryFacade.ConversationReadDto;
import static code.modules.conversation.ConversationQueryFacade.RequestReadDto;
import code.modules.conversation.data.ConversationEntity;
import code.modules.conversation.data.RequestEntity;
import code.modules.conversation.data.ResponseEntity;
import code.modules.conversation.service.Conversation;
import code.modules.conversation.service.Request;
import code.modules.conversation.service.Response;
import code.util.Generated;
import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = SpringMapperConfig.class)
@AnnotateWith(Generated.class)
public interface ConversationMapper {

  Request createDtoToDomain(ConversationCommandFacade.RequestGenerateDto createDto);

  @Mapping(target = "responses", source = "responses", ignore = true)
  RequestEntity domainToEntity(Request request);
  ConversationEntity domainToEntity(Conversation conversation);
  ResponseEntity domainToEntity(Response response);

  Request entityToDomain(RequestEntity save);
  @Mapping(target = "request", source = "request", ignore = true)
  Response entityToDomain(ResponseEntity saved);
  Conversation entityToDomain(ConversationEntity saved);

  Conversation createDtoToDomain(ConversationBeginDto conversationDto);

  ConversationReadDto domainToReadDto(Conversation conversation);
  RequestReadDto domainToReadDto(Request e);

}