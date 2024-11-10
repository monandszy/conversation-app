package code.modules.conversation.util;

import code.configuration.SpringMapperConfig;
import code.modules.conversation.ConversationCommandFacade.ConversationBeginDto;
import code.modules.conversation.ConversationQueryFacade.ConversationReadDto;
import static code.modules.conversation.ConversationQueryFacade.SectionReadDto;
import code.modules.conversation.data.ConversationEntity;
import code.modules.conversation.data.RequestEntity;
import code.modules.conversation.data.ResponseEntity;
import code.modules.conversation.data.SectionEntity;
import code.modules.conversation.data.jpa.SectionNavigationProjection;
import code.modules.conversation.service.Conversation;
import code.modules.conversation.service.Navigation;
import code.modules.conversation.service.Request;
import code.modules.conversation.service.Response;
import code.modules.conversation.service.Section;
import code.util.Generated;
import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;

@Mapper(config = SpringMapperConfig.class)
@AnnotateWith(Generated.class)
public interface ConversationMapper {

  ConversationReadDto domainToReadDto(Conversation domain);

  SectionReadDto domainToReadDto(Section domain);

  Conversation createDtoToDomain(ConversationBeginDto domain);

  SectionEntity domainToEntity(Section domain);

  ConversationEntity domainToEntity(Conversation domain);

  ResponseEntity domainToEntity(Response domain);

  RequestEntity domainToEntity(Request domain);

  Section entityToDomain(SectionEntity entity);

  Response entityToDomain(ResponseEntity entity);

  Request entityToDomain(RequestEntity entity);

  Conversation entityToDomain(ConversationEntity entity);

  default Section entityToDomain(SectionNavigationProjection entityDto) {
    return entityToDomain(entityDto.getSection())
      .withRequest(withRequestNavigationIds(entityDto)
        .withResponse(withResponseNavigationIds(entityDto)));
  }

  default Request withRequestNavigationIds(SectionNavigationProjection entityDto) {
    return entityToDomain(entityDto.getSelectedRequest())
      .withNavigation(Navigation.builder()
        .nextId(entityDto.getNextRequestId())
        .previousId(entityDto.getPrevRequestId())
        .build());
  }

  default Response withResponseNavigationIds(SectionNavigationProjection entityDto) {
    return entityToDomain(entityDto.getSelectedResponse())
      .withNavigation(Navigation.builder()
        .nextId(entityDto.getNextResponseId())
        .previousId(entityDto.getPrevResponseId())
        .build());
  }

}