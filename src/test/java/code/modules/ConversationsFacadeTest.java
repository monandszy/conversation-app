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
import code.modules.conversation.data.ConversationEntity;
import code.modules.conversation.data.ConversationRepository;
import code.modules.conversation.data.RequestEntity;
import code.modules.conversation.data.ResponseEntity;
import code.modules.conversation.data.jpa.ConversationJpaRepo;
import code.modules.conversation.data.jpa.RequestJpaRepo;
import code.modules.conversation.data.jpa.ResponseJpaRepo;
import code.modules.conversation.service.Conversation;
import code.modules.conversation.service.ConversationDao;
import code.modules.conversation.service.Request;
import code.modules.conversation.service.Response;
import code.modules.googleApi.GoogleApiAdapter;
import code.modules.googleApi.GoogleApiAdapter.ApiRequestDto;
import static code.modules.googleApi.GoogleApiAdapter.ApiResponseDto;
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
  private ConversationDao conversationDao;

  private ConversationRepository repository;
  private ConversationJpaRepo conversationJpaRepo;
  private RequestJpaRepo requestJpaRepo;
  private ResponseJpaRepo responseJpaRepo;

  @MockBean
  private GoogleApiAdapter googleApiAdapter;

  @Test
  void should_save_generated() {
    // given
    Conversation conversation = repository.create(Conversation.builder().accountId(UUID.randomUUID()).build());
    String requestText = "request";
    RequestGenerateDto requestDto = new RequestGenerateDto(requestText);
    ApiRequestDto apiRequestDto = new ApiRequestDto(requestText);
    String generatedText = "response";
    ApiResponseDto apiResponseDto = new ApiResponseDto(generatedText);
    // when
    Mockito.when(googleApiAdapter.generate(apiRequestDto)).thenReturn(apiResponseDto);
    RequestReadDto readDto = commandFacade.generate(requestDto, conversation.getId());
    // then
    RequestEntity request = requestJpaRepo.findAll().getFirst();
    ResponseEntity response = responseJpaRepo.findAll().getFirst();
    Mockito.verify(googleApiAdapter).generate(apiRequestDto);
    Assertions.assertEquals(requestText, readDto.text());
    Assertions.assertEquals(requestText, request.getText());
    Assertions.assertEquals(generatedText, readDto.responses().getFirst().text());
    Assertions.assertEquals(generatedText, response.getText());
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
  void should_return_request_page() {
    // given
    PageRequest pageRequest = PageRequest.of(0, Constants.PAGE_SIZE);
    Conversation conversation = Conversation.builder().accountId(UUID.randomUUID()).build();
    conversation = conversationDao.create(conversation);
    Response response = Response.builder().text("response").build();
    Request request = Request.builder()
      .conversation(conversation)
      .responses(List.of(response))
      .text("request").build();
    request = conversationDao.create(request);
    // when
    Page<RequestReadDto> requestPage = queryFacade.getRequestPage(pageRequest, new ConversationReadDto(conversation.getId()));
    // then
    assertThat(requestPage).isNotNull();
    assertThat(requestPage.getContent()).isNotEmpty();
    assertThat(requestPage.getContent().getFirst()).isNotNull();
    RequestReadDto requestDto = requestPage.getContent().getFirst();
    Assertions.assertEquals(requestDto.text(), request.getText());
    Assertions.assertEquals(requestDto.responses().getFirst().text(), response.getText());
  }
}