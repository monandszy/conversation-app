package code.configuration;

import code.modules.accounts.service.domain.AuthorityName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class DataInitializer implements ApplicationListener<ContextRefreshedEvent> {

  private DataSource dataSource;
  private static final String GET_AUTHORITIES = "SELECT name FROM authorities";

  @Override
  public void onApplicationEvent(final @NonNull ContextRefreshedEvent event) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    List<String> authoritiesToAdd = new ArrayList<>(
      Arrays.stream(AuthorityName.values()).map(Enum::name).toList());

    List<String> existingAuthorities = jdbcTemplate.queryForList(GET_AUTHORITIES, String.class);
    authoritiesToAdd.removeAll(existingAuthorities);

    if (!authoritiesToAdd.isEmpty()) {
      StringBuilder sql = new StringBuilder("INSERT INTO authorities (name) VALUES ");
      for (int i = 0; i < authoritiesToAdd.size(); i++) {
        log.info("Initializing authority: [{}]", authoritiesToAdd.get(i));
        sql.append("(?)");
        if (i < authoritiesToAdd.size() - 1) {
          sql.append(", "); // Add comma between values
        }
      }
      jdbcTemplate.update(sql.toString(), authoritiesToAdd.toArray());
    }
  }
}