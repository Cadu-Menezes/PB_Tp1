package com.cadu.pbtp1.preparo;

import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.interceptor.RetryInterceptorBuilder;

@Configuration
public class RabbitMqConfig {

    public static final String PEDIDOS_EXCHANGE = "pedidos.events";
    public static final String PREPARO_QUEUE = "preparo-service.pedidos";
    public static final String PREPARO_DEAD_LETTER_EXCHANGE = "pedidos.events.dlx";
    public static final String PREPARO_DEAD_LETTER_QUEUE = "preparo-service.pedidos.dlq";
    public static final String PREPARO_DEAD_LETTER_ROUTING_KEY = "preparo-service.pedidos.dlq";

    @Bean
    TopicExchange pedidosExchange() {
        return new TopicExchange(PEDIDOS_EXCHANGE, true, false);
    }

    @Bean
    Queue preparoQueue() {
        return new Queue(PREPARO_QUEUE, true, false, false, Map.of(
                "x-dead-letter-exchange", PREPARO_DEAD_LETTER_EXCHANGE,
                "x-dead-letter-routing-key", PREPARO_DEAD_LETTER_ROUTING_KEY));
    }

    @Bean
    TopicExchange preparoDeadLetterExchange() {
        return new TopicExchange(PREPARO_DEAD_LETTER_EXCHANGE, true, false);
    }

    @Bean
    Queue preparoDeadLetterQueue() {
        return new Queue(PREPARO_DEAD_LETTER_QUEUE, true);
    }

    @Bean
    Binding preparoDeadLetterBinding(@Qualifier("preparoDeadLetterQueue") Queue preparoDeadLetterQueue,
                                    @Qualifier("preparoDeadLetterExchange") TopicExchange preparoDeadLetterExchange) {
        return BindingBuilder.bind(preparoDeadLetterQueue).to(preparoDeadLetterExchange)
                .with(PREPARO_DEAD_LETTER_ROUTING_KEY);
    }

    @Bean
    SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setDefaultRequeueRejected(false);
        factory.setAdviceChain(RetryInterceptorBuilder.stateless()
                .maxAttempts(3)
                .backOffOptions(1000, 2.0, 10000)
                .build());
        return factory;
    }

    @Bean
    Binding pedidoCriadoBinding(@Qualifier("preparoQueue") Queue preparoQueue,
                                @Qualifier("pedidosExchange") TopicExchange pedidosExchange) {
        return BindingBuilder.bind(preparoQueue).to(pedidosExchange).with("pedido.criado");
    }

    @Bean
    Binding pedidoFinalizadoBinding(@Qualifier("preparoQueue") Queue preparoQueue,
                                    @Qualifier("pedidosExchange") TopicExchange pedidosExchange) {
        return BindingBuilder.bind(preparoQueue).to(pedidosExchange).with("pedido.finalizado");
    }

    @Bean
    Binding statusAtualizadoBinding(@Qualifier("preparoQueue") Queue preparoQueue,
                                    @Qualifier("pedidosExchange") TopicExchange pedidosExchange) {
        return BindingBuilder.bind(preparoQueue).to(pedidosExchange).with("preparo.status-atualizado");
    }
}