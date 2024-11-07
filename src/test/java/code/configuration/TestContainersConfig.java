package code.configuration;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;

@TestConfiguration
public class TestContainersConfig {

  static final String POSTGRES_VERSION = "postgres:16.3";
  static final String RABBITMQ_VERSION = "rabbitmq:4.0-rc";

  @Bean
  PostgreSQLContainer<?> postgresqlContainer() {
    return postgres;
  }

  @Bean
  RabbitMQContainer rabbitMqContainer() {
    return rabbitmq;
  }

  @ServiceConnection
  @SuppressWarnings("resource")
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(POSTGRES_VERSION)
    .withReuse(true);

  @ServiceConnection
  static RabbitMQContainer rabbitmq = new RabbitMQContainer(RABBITMQ_VERSION)
    .withReuse(true);

//  @DynamicPropertySource
//  static void registerRabbitMqProperties(DynamicPropertyRegistry registry) {
//  }

  @Bean
  DataSource dataSource(final PostgreSQLContainer<?> postgresqlContainer) {
    return DataSourceBuilder.create()
      .type(HikariDataSource.class)
      .driverClassName(postgresqlContainer.getDriverClassName())
      .url(postgresqlContainer.getJdbcUrl())
      .username(postgresqlContainer.getUsername())
      .password(postgresqlContainer.getPassword())
      .build();
  }
}