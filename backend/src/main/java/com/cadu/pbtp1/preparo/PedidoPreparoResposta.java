package com.cadu.pbtp1.preparo;

public record PedidoPreparoResposta(Long id, Long pedidoId, String descricao, StatusPreparo status, String atualizadoEm) {
}