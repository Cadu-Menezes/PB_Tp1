package com.cadu.pbtp1.preparo;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String PEDIDOS_EXCHANGE = "pedidos.events";
    public static final String PREPARO_QUEUE = "preparo-service.pedidos";

    @Bean
    TopicExchange pedidosExchange() {
        return new TopicExchange(PEDIDOS_EXCHANGE, true, false);
    }

    @Bean
    Queue preparoQueue() {
        return new Queue(PREPARO_QUEUE, true);
    }

    @Bean
    Binding pedidoCriadoBinding(Queue preparoQueue, TopicExchange pedidosExchange) {
        return BindingBuilder.bind(preparoQueue).to(pedidosExchange).with("pedido.criado");
    }

    @Bean
    Binding pedidoFinalizadoBinding(Queue preparoQueue, TopicExchange pedidosExchange) {
        return BindingBuilder.bind(preparoQueue).to(pedidosExchange).with("pedido.finalizado");
    }

    @Bean
    Binding statusAtualizadoBinding(Queue preparoQueue, TopicExchange pedidosExchange) {
        return BindingBuilder.bind(preparoQueue).to(pedidosExchange).with("preparo.status-atualizado");
    }
}