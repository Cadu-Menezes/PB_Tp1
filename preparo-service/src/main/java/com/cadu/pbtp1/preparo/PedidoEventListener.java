package com.cadu.pbtp1.preparo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PedidoEventListener {

    private static final Logger log = LoggerFactory.getLogger(PedidoEventListener.class);
    private static final String SERVICE_NAME = "preparo-service";
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
            log.info("[{}] Evento recebido | routingKey={} | pedidoId={} | status={}", SERVICE_NAME,
                    evento.eventType(), evento.pedidoId(), evento.status());
            String acao;
            switch (evento.eventType()) {
                case "pedido.criado" -> {
                    acao = "registrarPedido";
                    log.info("[{}] Processando pedido | pedidoId={} | acao={}", SERVICE_NAME,
                            evento.pedidoId(), acao);
                    pedidoPreparoService.registrarPedido(evento.pedidoId(), evento.descricao());
                }
                case "pedido.finalizado", "preparo.status-atualizado" -> {
                    acao = "atualizarStatus";
                    log.info("[{}] Processando pedido | pedidoId={} | status={} | acao={}", SERVICE_NAME,
                            evento.pedidoId(), evento.status(), acao);
                    pedidoPreparoService.atualizarStatus(evento.pedidoId(), evento.status());
                }
                default -> throw new IllegalArgumentException("Tipo de evento não suportado: " + evento.eventType());
            }
            log.info("[{}] Evento processado com sucesso | routingKey={} | pedidoId={} | status={}",
                    SERVICE_NAME, evento.eventType(), evento.pedidoId(), evento.status());
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Evento de pedido inválido.", exception);
        }
    }
}