package code.modules.conversation.util;

import code.configuration.SpringMapperConfig;
import code.modules.conversation.ConversationCommandFacade.ConversationBeginDto;
import code.modules.conversation.ConversationCommandFacade.RequestGenerateDto;
import code.modules.conversation.ConversationQueryFacade.ConversationReadDto;
import static code.modules.conversation.ConversationQueryFacade.SectionReadDto;
import code.modules.conversation.data.ConversationEntity;
import code.modules.conversation.data.RequestEntity;
import code.modules.conversation.data.ResponseEntity;
import code.modules.conversation.data.SectionEntity;
import code.modules.conversation.service.Conversation;
import code.modules.conversation.service.Request;
import code.modules.conversation.service.Response;
import code.modules.conversation.service.Section;
import code.util.Generated;
import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = SpringMapperConfig.class)
@AnnotateWith(Generated.class)
public interface ConversationMapper {

  Section createDtoToDomain(RequestGenerateDto generateDto);

  @Mapping(target = "responses", source = "responses", ignore = true)
  SectionEntity domainToEntity(Section domain);
  ConversationEntity domainToEntity(Conversation domain);
  ResponseEntity domainToEntity(Response domain);

  Section entityToDomain(SectionEntity entity);
  @Mapping(target = "conversationItem", source = "request", ignore = true)
  Response entityToDomain(ResponseEntity entity);
  Conversation entityToDomain(ConversationEntity entity);

  Conversation createDtoToDomain(ConversationBeginDto domain);

  ConversationReadDto domainToReadDto(Conversation domain);
  SectionReadDto domainToReadDto(Section domain);

  RequestEntity domainToEntity(Request domain);
}