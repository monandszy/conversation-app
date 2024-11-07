package code.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpIntegrationConfig {

  public static final String Q = "experimental";

  @Bean
  Binding binding(Queue queue, Exchange exchange) {
    return BindingBuilder.bind(queue).to(exchange).with(Q).noargs();
  }

  @Bean
  Exchange exchange() {
    return ExchangeBuilder.directExchange(Q).build();
  }

  @Bean
  Queue queue() {
    return QueueBuilder.durable(Q).build();
  }
}