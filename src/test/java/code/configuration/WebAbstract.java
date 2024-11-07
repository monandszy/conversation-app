package code.configuration;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import({TestSecurityConfig.class, UtilBeanConfig.class})
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class WebAbstract {
}