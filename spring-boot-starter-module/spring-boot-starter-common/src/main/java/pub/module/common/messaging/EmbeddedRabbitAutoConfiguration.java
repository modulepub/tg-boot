package pub.module.common.messaging;

import com.github.fridujo.rabbitmq.mock.MockConnectionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * 进程内 RabbitMQ（Mock AMQP），免外部安装，供 Spring Cloud Stream Rabbit Binder 使用。
 * <p>共享同一个 {@link MockConnectionFactory}，避免发布端与消费端各用一套内存 broker 导致消息丢失。</p>
 */
@AutoConfiguration(before = RabbitAutoConfiguration.class)
@ConditionalOnProperty(prefix = "tg.messaging.embedded-rabbit", name = "enabled", havingValue = "true", matchIfMissing = true)
public class EmbeddedRabbitAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    MockConnectionFactory tgEmbeddedMockConnectionFactory() {
        return new MockConnectionFactory();
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(ConnectionFactory.class)
    CachingConnectionFactory rabbitConnectionFactory(MockConnectionFactory tgEmbeddedMockConnectionFactory) {
        return new CachingConnectionFactory(tgEmbeddedMockConnectionFactory);
    }
}
