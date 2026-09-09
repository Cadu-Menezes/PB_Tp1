package com.cadu.pbtp1.preparo;

import com.cadu.pbtp1.PbTp1Application;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PedidoEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(PedidoEventPublisher.class);
    private static final String SERVICE_NAME = "backend";
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public PedidoEventPublisher(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void publicarPedidoCriado(Long pedidoId, String descricao) {
        publicar("pedido.criado", PedidoEvent.criado(pedidoId, descricao));
    }

    public void publicarPedidoFinalizado(Long pedidoId) {
        publicar("pedido.finalizado", PedidoEvent.finalizado(pedidoId));
    }

    public void publicarStatusAtualizado(Long pedidoId, StatusPreparo status) {
        publicar("preparo.status-atualizado", PedidoEvent.statusAtualizado(pedidoId, status));
    }

    private void publicar(String routingKey, PedidoEvent evento) {
        try {
            String mensagem = objectMapper.writeValueAsString(evento);
            log.info("[{}] Publicando evento | exchange={} | routingKey={} | pedidoId={} | tipo={} | status={}",
                    SERVICE_NAME, PbTp1Application.PEDIDOS_EXCHANGE, routingKey, evento.pedidoId(),
                    evento.eventType(), evento.status());
            rabbitTemplate.convertAndSend(PbTp1Application.PEDIDOS_EXCHANGE, routingKey,
                    mensagem);
            log.info("[{}] Evento publicado com sucesso | routingKey={} | pedidoId={} | tipo={}",
                    SERVICE_NAME, routingKey, evento.pedidoId(), evento.eventType());
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Não foi possível serializar o evento do pedido.", exception);
        }
    }
}