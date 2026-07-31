package com.cadu.pbtp1.pedido;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoHistoricoRepository pedidoHistoricoRepository;

    public PedidoService(PedidoRepository pedidoRepository, PedidoHistoricoRepository pedidoHistoricoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoHistoricoRepository = pedidoHistoricoRepository;
    }

    @Transactional(readOnly = true)
    public List<Pedido> listar() {
        return pedidoRepository.findAll();
    }

    @Transactional
    public Pedido criar(String descricao) {
        Pedido pedido = new Pedido(descricao);
        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        registrarHistorico(pedidoSalvo, PedidoHistorico.TipoEvento.CRIADO);
        return pedidoSalvo;
    }

    @Transactional
    public Pedido finalizar(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado."));
        pedido.finalizar();
        Pedido pedidoFinalizado = pedidoRepository.save(pedido);
        registrarHistorico(pedidoFinalizado, PedidoHistorico.TipoEvento.FINALIZADO);
        return pedidoFinalizado;
    }

    @Transactional(readOnly = true)
    public List<PedidoHistorico> listarHistorico(Long pedidoId) {
        return pedidoHistoricoRepository.findByPedidoIdOrderByAlteradoEmAsc(pedidoId);
    }

    private void registrarHistorico(Pedido pedido, PedidoHistorico.TipoEvento tipoEvento) {
        PedidoHistorico historico = new PedidoHistorico(
                pedido.getId(),
                pedido.getDescricao(),
                pedido.isFinalizado(),
                tipoEvento,
                LocalDateTime.now()
        );
        pedidoHistoricoRepository.save(historico);
    }
}
