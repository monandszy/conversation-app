package code.modules;

import code.configuration.Constants;
import code.configuration.ContextConfig;
import code.configuration.FacadeAbstract;
import code.configuration.UtilBeanConfig;
import code.modules.conversation.ConversationCommandFacadeI;
import code.modules.conversation.ConversationCommandFacadeI.ConversationBeginDto;
import code.modules.conversation.ConversationCommandFacadeI.RequestGenerateDto;
import code.modules.conversation.ConversationQueryFacadeI;
import code.modules.conversation.ConversationQueryFacadeI.ConversationReadDto;
import code.modules.conversation.ConversationQueryFacadeI.RequestReadDto;
import code.modules.conversation.ConversationQueryFacadeI.ResponseReadDto;
import code.modules.conversation.ConversationQueryFacadeI.SectionReadDto;
import code.modules.conversation.data.CommandConversationRepo;
import code.modules.conversation.data.entity.ConversationEntity;
import code.modules.conversation.data.jpa.ConversationJpaRepo;
import code.modules.conversation.data.jpa.SectionJpaRepo;
import code.modules.conversation.service.domain.Conversation;
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

  private ConversationQueryFacadeI queryFacade;
  private ConversationCommandFacadeI commandFacade;

  private CommandConversationRepo repository;
  private ConversationJpaRepo conversationJpaRepo;
  private SectionJpaRepo sectionJpaRepo;

  @MockBean
  private GoogleApiAdapter googleApiAdapter;

  // God forgive me for I have sinned...
  @Test
  void should_save_and_retrieve_generated() {
    // given
    PageRequest pageRequest = PageRequest.of(0, Constants.PAGE_SIZE);
    UUID accountId = UUID.randomUUID();
    Conversation conversation = repository.create(Conversation.builder().created(OffsetDateTime.now()).accountId(accountId).build());
    UUID conversationId = conversation.getId();
    String requestText = "request";
    String generatedText = "response";
    RequestGenerateDto generateDto = new RequestGenerateDto(requestText);
    ApiRequestDto apiRequestDto = new ApiRequestDto(requestText);
    ApiResponseDto apiResponseDto = new ApiResponseDto(generatedText);
    // when
    Mockito.when(googleApiAdapter.generate(apiRequestDto)).thenReturn(apiResponseDto);
    SectionReadDto firstSection = commandFacade.generate(generateDto, conversationId);
    // then
    Mockito.verify(googleApiAdapter).generate(apiRequestDto);
    List<RequestReadDto> requests = firstSection.requests();
    assertThat(requests).isNotEmpty();
    RequestReadDto firstRequest = requests.getFirst();
    List<ResponseReadDto> responses = firstRequest.responses();
    assertThat(responses).isNotEmpty();
    ResponseReadDto firstResponse = responses.getFirst();
    Assertions.assertEquals(requestText, firstRequest.text());
    Assertions.assertEquals(generatedText, firstResponse.text());

    //given
    RequestReadDto request = firstSection.requests().getFirst();
    //when
    commandFacade.retry(request.id());
    Page<SectionReadDto> retryPage = queryFacade.getSectionPage(pageRequest, conversationId);
    //then
    assertThat(retryPage).isNotNull();
    List<SectionReadDto> retrySections = retryPage.getContent();
    assertThat(retrySections).isNotEmpty();
    SectionReadDto retrySection = retrySections.getFirst();
    assertThat(retrySection.id()).isEqualTo(firstSection.id());
    List<RequestReadDto> retryRequests = retrySection.requests();
    assertThat(retryRequests).isNotEmpty();
    RequestReadDto retryRequest = retryRequests.getFirst();
    List<ResponseReadDto> retryResponses = retryRequest.responses();
    assertThat(retryResponses).isNotEmpty();
    ResponseReadDto retryResponse = retryResponses.getFirst();
    assertThat(retryResponse.id().equals(firstResponse.id())).isFalse();
    assertThat(retryResponse.navigation().previousId()).isEqualTo(firstResponse.id());

    //given
    RequestGenerateDto regenerateDto = new RequestGenerateDto(requestText);
    //when
    commandFacade.regenerate(regenerateDto,  firstSection.id());
    Page<SectionReadDto> regeneratePage = queryFacade.getSectionPage(pageRequest, conversationId);
    //then
    assertThat(retryPage).isNotNull();
    List<SectionReadDto> reGenSections = regeneratePage.getContent();
    SectionReadDto reGenSection = reGenSections.getFirst();
    assertThat(reGenSection.id()).isEqualTo(firstSection.id());
    List<RequestReadDto> reGenRequests = reGenSection.requests();
    RequestReadDto reGenRequest = reGenRequests.getFirst();
    assertThat(reGenRequest.id().equals(firstRequest.id())).isFalse();
    List<ResponseReadDto> reGenResponses = reGenRequest.responses();
    ResponseReadDto reGenResponse = reGenResponses.getFirst();
    assertThat(reGenResponse.id().equals(firstResponse.id())).isFalse();
    assertThat(reGenResponse.navigation().previousId()).isNull();
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