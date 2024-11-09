package code.modules;

import code.configuration.Constants;
import code.configuration.ContextConfig;
import code.configuration.FacadeAbstract;
import code.configuration.UtilBeanConfig;
import code.modules.conversation.ConversationCommandFacade;
import code.modules.conversation.ConversationCommandFacade.ConversationBeginDto;
import code.modules.conversation.ConversationCommandFacade.RequestGenerateDto;
import code.modules.conversation.ConversationQueryFacade;
import code.modules.conversation.ConversationQueryFacade.ConversationReadDto;
import code.modules.conversation.ConversationQueryFacade.RequestReadDto;
import static code.modules.conversation.ConversationQueryFacade.ResponseReadDto;
import code.modules.conversation.ConversationQueryFacade.SectionReadDto;
import code.modules.conversation.data.ConversationEntity;
import code.modules.conversation.data.ConversationRepository;
import code.modules.conversation.data.RequestEntity;
import code.modules.conversation.data.ResponseEntity;
import code.modules.conversation.data.SectionEntity;
import code.modules.conversation.data.jpa.ConversationJpaRepo;
import code.modules.conversation.data.jpa.SectionJpaRepo;
import code.modules.conversation.service.Conversation;
import code.modules.conversation.service.ConversationDao;
import code.modules.conversation.service.Request;
import code.modules.conversation.service.Response;
import code.modules.conversation.service.Section;
import code.modules.googleApi.GoogleApiAdapter;
import code.modules.googleApi.GoogleApiAdapter.ApiRequestDto;
import static code.modules.googleApi.GoogleApiAdapter.ApiResponseDto;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Slf4j
@Import({ContextConfig.ConversationsModuleContext.class, UtilBeanConfig.class})
@AllArgsConstructor(onConstructor = @__(@Autowired))
class ConversationsFacadeTest extends FacadeAbstract {

  private ConversationQueryFacade queryFacade;
  private ConversationCommandFacade commandFacade;
  private ConversationDao conversationDao;

  private ConversationRepository repository;
  private ConversationJpaRepo conversationJpaRepo;
  private SectionJpaRepo sectionJpaRepo;

  @MockBean
  private GoogleApiAdapter googleApiAdapter;

  @Test
  void should_save_generated() {
    // given
    UUID conversationId = UUID.randomUUID();
    Conversation conversation = repository.create(Conversation.builder().accountId(conversationId).build());
    String requestText = "request";
    RequestGenerateDto requestDto = new RequestGenerateDto(requestText, conversation.getId());
    ApiRequestDto apiRequestDto = new ApiRequestDto(requestText);
    String generatedText = "response";
    ApiResponseDto apiResponseDto = new ApiResponseDto(generatedText);
    // when
    Mockito.when(googleApiAdapter.generate(apiRequestDto)).thenReturn(apiResponseDto);
    SectionReadDto readDto = commandFacade.generate(requestDto);
    // then
    SectionEntity sectionEntity = sectionJpaRepo.findAll().getFirst();
    RequestEntity requestEntity = sectionEntity.getRequests().stream().findAny().orElseThrow();
    ResponseEntity responseEntity = requestEntity.getResponses().stream().findAny().orElseThrow();

    Mockito.verify(googleApiAdapter).generate(apiRequestDto);

    RequestReadDto first = readDto.requests().getFirst();
    Assertions.assertEquals(requestText, first.text());
    Assertions.assertEquals(requestText, requestEntity.getText());
    Assertions.assertEquals(generatedText, first.responses().getFirst().text());
    Assertions.assertEquals(generatedText, responseEntity.getText());

    Assertions.assertNotNull(sectionEntity.getCreated());
    Assertions.assertEquals(conversationId, sectionEntity.getConversation().getId());
  }

  @Test
  void should_begin_conversation() {
    // given
    ConversationBeginDto createDto = new ConversationBeginDto(UUID.randomUUID());
    // when
    ConversationReadDto readDto = commandFacade.begin(createDto);
    // then
    assertThat(readDto).isNotNull();
    ConversationEntity entity = conversationJpaRepo.findAll().getFirst();
    assertThat(entity).isNotNull();
    assertThat(entity.getId()).isNotNull();
    assertThat(entity.getCreated()).isNotNull();
  }

  @Test
  void should_return_conversation_page() {
    // given
    UUID accountId = UUID.randomUUID();
    conversationDao.create(Conversation.builder().accountId(accountId).build());
    PageRequest pageRequest = PageRequest.of(0, Constants.PAGE_SIZE);
    // when
    Page<ConversationReadDto> conversationPage = queryFacade.getConversationPage(pageRequest, accountId);
    // then
    assertThat(conversationPage).isNotNull();
    assertThat(conversationPage.getContent()).isNotEmpty();
    assertThat(conversationPage.getContent().getFirst().id()).isNotNull();
  }

  @Test
  void should_return_section_page() {
    // given
    PageRequest pageRequest = PageRequest.of(0, Constants.PAGE_SIZE);
    UUID conversationId = UUID.randomUUID();
    Conversation conversation = Conversation.builder().accountId(conversationId).build();
    conversation = conversationDao.create(conversation);

    String requestText = "request";
    String responseText = "response";
    Response response = Response.builder().text(responseText).build();
    Request request = Request.builder().text(requestText).responses(Set.of(response)).build();
    Section section = Section.builder()
      .conversation(conversation)
      .requests(Set.of(request))
      .build();
    conversationDao.create(section);
    // when
    Page<SectionReadDto> requestPage = queryFacade.getSectionPage(pageRequest, new ConversationReadDto(conversation.getId()));
    // then
    assertThat(requestPage).isNotNull();
    List<SectionReadDto> sections = requestPage.getContent();
    assertThat(sections).isNotEmpty();
    List<RequestReadDto> requests = sections.getFirst().requests();
    assertThat(requests).isNotEmpty();
    List<ResponseReadDto> responses = requests.getFirst().responses();
    assertThat(responses).isNotEmpty();

  }

}