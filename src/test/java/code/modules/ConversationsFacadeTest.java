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
import code.modules.conversation.ConversationQueryFacade.ResponseReadDto;
import code.modules.conversation.ConversationQueryFacade.SectionReadDto;
import code.modules.conversation.data.ConversationEntity;
import code.modules.conversation.data.ConversationRepository;
import code.modules.conversation.data.jpa.ConversationJpaRepo;
import code.modules.conversation.data.jpa.SectionJpaRepo;
import code.modules.conversation.service.Conversation;
import code.modules.googleApi.GoogleApiAdapter;
import code.modules.googleApi.GoogleApiAdapter.ApiRequestDto;
import static code.modules.googleApi.GoogleApiAdapter.ApiResponseDto;
import java.time.OffsetDateTime;
import java.util.List;
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

  private ConversationRepository repository;
  private ConversationJpaRepo conversationJpaRepo;
  private SectionJpaRepo sectionJpaRepo;

  @MockBean
  private GoogleApiAdapter googleApiAdapter;

  @Test
  void should_save_and_retrieve_generated() {
    // given
    PageRequest pageRequest = PageRequest.of(0, Constants.PAGE_SIZE);
    UUID accountId = UUID.randomUUID();
    Conversation conversation = repository.create(Conversation.builder().created(OffsetDateTime.now()).accountId(accountId).build());
    UUID conversationId = conversation.getId();
    String requestText = "request";
    RequestGenerateDto requestDto = new RequestGenerateDto(requestText, conversationId);
    ApiRequestDto apiRequestDto = new ApiRequestDto(requestText);
    String generatedText = "response";
    ApiResponseDto apiResponseDto = new ApiResponseDto(generatedText);
    ConversationReadDto inputReadDto = new ConversationReadDto(conversationId);
    // when
    Mockito.when(googleApiAdapter.generate(apiRequestDto)).thenReturn(apiResponseDto);
    commandFacade.generate(requestDto);
    Page<SectionReadDto> requestPage = queryFacade.getSectionPage(pageRequest, inputReadDto);
    // then
    Mockito.verify(googleApiAdapter).generate(apiRequestDto);

    assertThat(requestPage).isNotNull();
    List<SectionReadDto> sections = requestPage.getContent();
    assertThat(sections).isNotEmpty();
    SectionReadDto firstSection = sections.getFirst();
    RequestReadDto request = firstSection.request();
    assertThat(request).isNotNull();
    ResponseReadDto response = request.response();
    assertThat(response).isNotNull();

    Assertions.assertEquals(requestText, request.text());
    Assertions.assertEquals(generatedText, response.text());
  }

  @Test
  void should_begin_and_return_conversation() {
    // given
    UUID accountId = UUID.randomUUID();
    PageRequest pageRequest = PageRequest.of(0, Constants.PAGE_SIZE);
    ConversationBeginDto createDto = new ConversationBeginDto(accountId);
    // when
    ConversationReadDto readDto = commandFacade.begin(createDto);
    Page<ConversationReadDto> conversationPage = queryFacade.getConversationPage(pageRequest, accountId);
    // then
    assertThat(readDto).isNotNull();
    ConversationEntity entity = conversationJpaRepo.findAll().getFirst();
    assertThat(entity).isNotNull();
    assertThat(entity.getId()).isNotNull();
    assertThat(entity.getCreated()).isNotNull();

    assertThat(conversationPage).isNotNull();
    assertThat(conversationPage.getContent()).isNotEmpty();
    assertThat(conversationPage.getContent().getFirst().id()).isNotNull();
  }

}