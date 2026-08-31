package com.cadu.pbtp1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;

@SpringBootApplication
@EnableRabbit
public class PreparoServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PreparoServiceApplication.class, args);
    }
}