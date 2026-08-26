package com.cadu.pbtp1.preparo;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PedidoPreparoService {

    private final PedidoPreparoRepository pedidoPreparoRepository;

    public PedidoPreparoService(PedidoPreparoRepository pedidoPreparoRepository) {
        this.pedidoPreparoRepository = pedidoPreparoRepository;
    }

    @Transactional(readOnly = true)
    public List<PedidoPreparo> listar() {
        return pedidoPreparoRepository.findAllByOrderByAtualizadoEmDesc();
    }

    @Transactional(readOnly = true)
    public PedidoPreparo obter(Long pedidoId) {
        return pedidoPreparoRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido de preparo não encontrado."));
    }

    @Transactional
    public PedidoPreparo registrarPedido(Long pedidoId, String descricao) {
        PedidoPreparo pedidoPreparo = pedidoPreparoRepository.findByPedidoId(pedidoId)
                .orElse(new PedidoPreparo(pedidoId, descricao, StatusPreparo.RECEBIDO));

        pedidoPreparo.atualizarDescricao(descricao);
        pedidoPreparo.atualizarStatus(StatusPreparo.RECEBIDO);
        return pedidoPreparoRepository.save(pedidoPreparo);
    }

    @Transactional
    public PedidoPreparo atualizarStatus(Long pedidoId, StatusPreparo status) {
        PedidoPreparo pedidoPreparo = obter(pedidoId);
        pedidoPreparo.atualizarStatus(status);
        return pedidoPreparoRepository.save(pedidoPreparo);
    }
}