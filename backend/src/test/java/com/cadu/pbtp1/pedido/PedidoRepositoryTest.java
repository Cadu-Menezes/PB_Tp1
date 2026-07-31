package com.cadu.pbtp1.pedido;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class PedidoRepositoryTest {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PedidoHistoricoRepository pedidoHistoricoRepository;

    @Test
    void deveSalvarPedidoEmBuscarNovamente() {
        Pedido pedido = new Pedido("Combo hamburguer");

        Pedido salvo = pedidoRepository.save(pedido);

        assertThat(salvo.getId()).isNotNull();
        assertThat(pedidoRepository.findById(salvo.getId())).isPresent();
    }

    @Test
    void deveListarHistoricoOrdenadoPorData() {
        PedidoHistorico primeiro = new PedidoHistorico(1L, "Pedido 1", false, PedidoHistorico.TipoEvento.CRIADO, LocalDateTime.of(2026, 7, 1, 10, 0));
        PedidoHistorico segundo = new PedidoHistorico(1L, "Pedido 1", true, PedidoHistorico.TipoEvento.FINALIZADO, LocalDateTime.of(2026, 7, 1, 10, 5));

        pedidoHistoricoRepository.save(primeiro);
        pedidoHistoricoRepository.save(segundo);

        assertThat(pedidoHistoricoRepository.findByPedidoIdOrderByAlteradoEmAsc(1L))
                .extracting(PedidoHistorico::getTipoEvento)
                .containsExactly(PedidoHistorico.TipoEvento.CRIADO, PedidoHistorico.TipoEvento.FINALIZADO);
    }
}