package com.cadu.pbtp1.pedido;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.cadu.pbtp1.preparo.PedidoEventPublisher;
import com.cadu.pbtp1.preparo.StatusPreparo;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoHistoricoRepository pedidoHistoricoRepository;
    private final PedidoEventPublisher pedidoEventPublisher;

    public PedidoService(PedidoRepository pedidoRepository, PedidoHistoricoRepository pedidoHistoricoRepository, PedidoEventPublisher pedidoEventPublisher) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoHistoricoRepository = pedidoHistoricoRepository;
        this.pedidoEventPublisher = pedidoEventPublisher;
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
        pedidoEventPublisher.publicarPedidoCriado(pedidoSalvo.getId(), pedidoSalvo.getDescricao());
        return pedidoSalvo;
    }

    @Transactional
    public Pedido finalizar(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado."));
        pedido.finalizar();
        Pedido pedidoFinalizado = pedidoRepository.save(pedido);
        registrarHistorico(pedidoFinalizado, PedidoHistorico.TipoEvento.FINALIZADO);
        pedidoEventPublisher.publicarPedidoFinalizado(pedidoFinalizado.getId());
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
