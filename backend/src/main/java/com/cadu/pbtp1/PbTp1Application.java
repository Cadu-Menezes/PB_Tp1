package com.cadu.pbtp1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableFeignClients
public class PbTp1Application {

    public static final String PEDIDOS_EXCHANGE = "pedidos.events";

    @Bean
    TopicExchange pedidosExchange() {
        return new TopicExchange(PEDIDOS_EXCHANGE, true, false);
    }

    public static void main(String[] args) {
        SpringApplication.run(PbTp1Application.class, args);
    }
}
