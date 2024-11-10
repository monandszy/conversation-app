package code.modules.conversation;

import code.modules.conversation.service.Conversation;
import code.modules.conversation.service.ConversationDao;
import code.modules.conversation.service.Section;
import code.modules.conversation.util.ConversationMapper;
import code.util.Facade;
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
    Page<Section> sectionPage = conversationDao.getSectionPage(pageRequest, conversation);
    Page<SectionReadDto> page = sectionPage.map(e -> mapper.domainToReadDto(e));
    log.info("Conversation Item Page: {}", page);
    return page;
  }

  public record ConversationReadDto(
    UUID id
  ) {}

  public record SectionReadDto(
    RequestReadDto request
  ) {}

  public record RequestReadDto(
    String text,
    ResponseReadDto response,
    NavigationDto navigation
  ) {}

  public record ResponseReadDto(
    String text,
    NavigationDto navigation
  ) {}

  public record NavigationDto(
    UUID nextId,
    UUID previousId
  ) {}
}