package code.modules;

import static code.modules.google_api.GoogleApiAdapter.ApiResponseDto;
import static org.assertj.core.api.Assertions.assertThat;

import code.configuration.Constants;
import code.configuration.ContextConfig;
import code.configuration.FacadeAbstract;
import code.configuration.UtilBeanConfig;
import code.modules.conversation.IConversationCommandFacade;
import code.modules.conversation.IConversationCommandFacade.ConversationBeginDto;
import code.modules.conversation.IConversationCommandFacade.RequestGenerateDto;
import code.modules.conversation.IConversationQueryFacade;
import code.modules.conversation.IConversationQueryFacade.ConversationReadDto;
import code.modules.conversation.IConversationQueryFacade.RequestReadDto;
import code.modules.conversation.IConversationQueryFacade.ResponseReadDto;
import code.modules.conversation.IConversationQueryFacade.SectionReadDto;
import code.modules.conversation.data.CommandConversationRepo;
import code.modules.conversation.data.entity.ConversationEntity;
import code.modules.conversation.data.jpa.ConversationJpaRepo;
import code.modules.conversation.service.domain.Conversation;
import code.modules.google_api.GoogleApiAdapter;
import code.modules.google_api.GoogleApiAdapter.ApiRequestDto;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

  private IConversationQueryFacade queryFacade;
  private IConversationCommandFacade commandFacade;

  private CommandConversationRepo repository;
  private ConversationJpaRepo conversationJpaRepo;

  @MockBean
  private GoogleApiAdapter googleApiAdapter;

  @Test
  void should_save_and_retrieve_generated() {
    // Setup
    PageRequest pageRequest = PageRequest.of(0, Constants.PAGE_SIZE);
    UUID accountId = UUID.randomUUID();
    OffsetDateTime now = OffsetDateTime.now();

    // Create conversation
    Conversation conversation = repository.create(
      Conversation.builder()
        .created(now)
        .accountId(accountId)
        .build()
    );
    UUID conversationId = conversation.getId();

    // Initial Request-Response generation
    String requestText = "request";
    String generatedText = "response";
    RequestGenerateDto generateDto = new RequestGenerateDto(requestText);
    ApiRequestDto apiRequestDto = new ApiRequestDto(requestText);
    ApiResponseDto apiResponseDto = new ApiResponseDto(generatedText);

    // Mock API response
    Mockito.when(googleApiAdapter.generate(apiRequestDto)).thenReturn(apiResponseDto);

    // Generate the first section
    SectionReadDto firstSection = commandFacade.generate(generateDto, conversationId);

    // Verify API call and validate generated content
    Mockito.verify(googleApiAdapter).generate(apiRequestDto);
    assertGeneratedSection(firstSection, requestText, generatedText);

    // Retry request and validate changes
    retryRequestAndValidate(firstSection, pageRequest, conversationId);

    // Regenerate content and validate changes
    regenerateRequestAndValidate(firstSection, requestText, pageRequest, conversationId);
  }

  private void assertGeneratedSection(
    SectionReadDto section,
    String expectedRequestText,
    String expectedResponseText
  ) {
    assertThat(section.requests()).isNotEmpty();
    RequestReadDto firstRequest = section.requests().getFirst();

    assertThat(firstRequest.responses()).isNotEmpty();
    ResponseReadDto firstResponse = firstRequest.responses().getFirst();

    Assertions.assertEquals(expectedRequestText, firstRequest.text());
    Assertions.assertEquals(expectedResponseText, firstResponse.text());
  }

  private void retryRequestAndValidate(
    SectionReadDto initialSection,
    PageRequest pageRequest,
    UUID conversationId
  ) {
    // Retry
    RequestReadDto firstRequest = initialSection.requests().getFirst();
    commandFacade.retry(firstRequest.id());

    // Fetch updated sections
    Page<SectionReadDto> retryPage = queryFacade.getSectionPage(pageRequest, conversationId);
    assertThat(retryPage).isNotNull();

    // Validate retry changes
    SectionReadDto retrySection = retryPage.getContent().getFirst();
    assertThat(retrySection.id()).isEqualTo(initialSection.id());

    RequestReadDto retryRequest = retrySection.requests().getFirst();
    ResponseReadDto retryResponse = retryRequest.responses().getFirst();

    ResponseReadDto initialResponse = firstRequest.responses().getFirst();

    // Ensure the retry produced a new response with navigation linking back to the previous one
    assertThat(retryResponse.id()).isNotEqualTo(initialResponse.id());
    assertThat(retryResponse.navigation().previousId()).isEqualTo(initialResponse.id());
  }

  private void regenerateRequestAndValidate(
    SectionReadDto initialSection,
    String requestText,
    PageRequest pageRequest,
    UUID conversationId
  ) {
    // Regenerate
    RequestGenerateDto regenerateDto = new RequestGenerateDto(requestText);
    commandFacade.regenerate(regenerateDto, initialSection.id());

    // Fetch updated sections
    Page<SectionReadDto> regeneratePage = queryFacade.getSectionPage(pageRequest, conversationId);
    assertThat(regeneratePage).isNotNull();

    // Validate regenerated changes
    SectionReadDto regeneratedSection = regeneratePage.getContent().getFirst();
    assertThat(regeneratedSection.id()).isEqualTo(initialSection.id());

    RequestReadDto regeneratedRequest = regeneratedSection.requests().getFirst();
    ResponseReadDto regeneratedResponse = regeneratedRequest.responses().getFirst();

    RequestReadDto initialRequest = initialSection.requests().getFirst();
    ResponseReadDto initialResponse = initialRequest.responses().getFirst();

    // Ensure the regeneration produced a completely new request and response
    assertThat(regeneratedRequest.id()).isNotEqualTo(initialRequest.id());
    assertThat(regeneratedResponse.id()).isNotEqualTo(initialResponse.id());
    assertThat(regeneratedResponse.navigation().previousId()).isNull();
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