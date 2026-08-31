package com.cadu.pbtp1.preparo;

import java.time.Instant;

public record PedidoEvent(String eventId, String eventType, Instant occurredAt, Long pedidoId, String descricao,
                          StatusPreparo status) {
}