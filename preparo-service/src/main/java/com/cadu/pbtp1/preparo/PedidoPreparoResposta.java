package com.cadu.pbtp1.preparo;

public record PedidoPreparoResposta(Long id, Long pedidoId, String descricao, StatusPreparo status, String atualizadoEm) {

    public PedidoPreparoResposta(PedidoPreparo pedidoPreparo) {
        this(
                pedidoPreparo.getId(),
                pedidoPreparo.getPedidoId(),
                pedidoPreparo.getDescricao(),
                pedidoPreparo.getStatus(),
                pedidoPreparo.getAtualizadoEm().toString()
        );
    }
}