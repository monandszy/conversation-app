package code.modules;

import code.configuration.ContextConfig;
import code.configuration.FacadeAbstract;
import code.configuration.UtilBeanConfig;
import code.modules.conversation.IConversationCommandFacade;
import code.modules.conversation.IConversationQueryFacade;
import code.modules.conversation.data.CommandConversationRepo;
import code.modules.conversation.data.jpa.ConversationJpaRepo;
import code.modules.google_api.GoogleApiAdapter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

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
  
}