package code.modules.conversation;

import code.modules.conversation.service.Conversation;
import code.modules.conversation.service.ConversationDao;
import code.modules.conversation.util.ConversationMapper;
import code.util.Facade;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Facade
@Slf4j
@AllArgsConstructor
public class ConversationQueryFacade {

  private ConversationDao conversationDao;
  private ConversationMapper mapper;

  public Page<ConversationReadDto> getConversationPage(PageRequest pageRequest, UUID accountId) {
    Page<ConversationReadDto> page = conversationDao.getConversationPage(pageRequest, accountId)
      .map(e -> mapper.domainToReadDto(e));
    log.info("Conversation Page: {}", page);
    return page;
  }

  public Page<SectionReadDto> getSectionPage(PageRequest pageRequest, ConversationReadDto readDto) {
    Conversation conversation = Conversation.builder().id(readDto.id()).build();
    Page<SectionReadDto> page = conversationDao.getSectionPage(pageRequest, conversation)
      .map(e -> mapper.domainToReadDto(e));
    log.info("Conversation Item Page: {}", page);
    return page;
  }

  public record ConversationReadDto(
    UUID id
  ) {}

  public record SectionReadDto(
    List<RequestReadDto> requests
  ) {}

  public record RequestReadDto(
    String text,
    List<ResponseReadDto> responses
  ) {}

  public record ResponseReadDto(
    String text
  ) {}
}