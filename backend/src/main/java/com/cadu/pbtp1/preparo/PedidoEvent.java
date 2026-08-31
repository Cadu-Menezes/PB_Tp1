package com.cadu.pbtp1.preparo;

import java.time.Instant;
import java.util.UUID;

public record PedidoEvent(String eventId, String eventType, Instant occurredAt, Long pedidoId, String descricao,
                          StatusPreparo status) {

    public static PedidoEvent criado(Long pedidoId, String descricao) {
        return new PedidoEvent(UUID.randomUUID().toString(), "pedido.criado", Instant.now(), pedidoId, descricao,
                StatusPreparo.RECEBIDO);
    }

    public static PedidoEvent finalizado(Long pedidoId) {
        return new PedidoEvent(UUID.randomUUID().toString(), "pedido.finalizado", Instant.now(), pedidoId, null,
                StatusPreparo.ENTREGUE);
    }

    public static PedidoEvent statusAtualizado(Long pedidoId, StatusPreparo status) {
        return new PedidoEvent(UUID.randomUUID().toString(), "preparo.status-atualizado", Instant.now(), pedidoId,
                null, status);
    }
}