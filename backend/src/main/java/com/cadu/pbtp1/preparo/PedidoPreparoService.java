package com.cadu.pbtp1.preparo;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class PedidoPreparoService {

    private final PedidoPreparoClient pedidoPreparoClient;

    public PedidoPreparoService(PedidoPreparoClient pedidoPreparoClient) {
        this.pedidoPreparoClient = pedidoPreparoClient;
    }

    public List<PedidoPreparoResposta> listar() {
        return pedidoPreparoClient.listar();
    }

    public PedidoPreparoResposta obter(Long pedidoId) {
        return pedidoPreparoClient.obterPorPedidoId(pedidoId);
    }

    public PedidoPreparoResposta registrarPedido(Long pedidoId, String descricao) {
        return pedidoPreparoClient.registrar(new NovoPedidoPreparoRequest(pedidoId, descricao));
    }

    public PedidoPreparoResposta atualizarStatus(Long pedidoId, StatusPreparo status) {
        return pedidoPreparoClient.atualizarStatus(pedidoId, new AtualizarStatusPreparoRequest(status));
    }
}