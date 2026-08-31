package com.cadu.pbtp1.preparo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PedidoEventListener {

    private final PedidoPreparoService pedidoPreparoService;
    private final ObjectMapper objectMapper;

    public PedidoEventListener(PedidoPreparoService pedidoPreparoService, ObjectMapper objectMapper) {
        this.pedidoPreparoService = pedidoPreparoService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitMqConfig.PREPARO_QUEUE)
    public void receber(String mensagem) {
        try {
            PedidoEvent evento = objectMapper.readValue(mensagem, PedidoEvent.class);
            switch (evento.eventType()) {
                case "pedido.criado" -> pedidoPreparoService.registrarPedido(evento.pedidoId(), evento.descricao());
                case "pedido.finalizado", "preparo.status-atualizado" ->
                        pedidoPreparoService.atualizarStatus(evento.pedidoId(), evento.status());
                default -> throw new IllegalArgumentException("Tipo de evento não suportado: " + evento.eventType());
            }
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Evento de pedido inválido.", exception);
        }
    }
}