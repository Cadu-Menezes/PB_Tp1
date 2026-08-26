package com.cadu.pbtp1.pedido;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class PedidoServiceTest {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PedidoHistoricoRepository pedidoHistoricoRepository;

    @MockBean
    private com.cadu.pbtp1.preparo.PedidoPreparoService pedidoPreparoService;

    @Test
    void deveCriarPedidoERegistrarHistorico() {
        limparDados();

        Pedido pedido = pedidoService.criar("Batata frita");

        assertThat(pedido.getId()).isNotNull();
        assertThat(pedidoHistoricoRepository.findByPedidoIdOrderByAlteradoEmAsc(pedido.getId()))
                .hasSize(1)
                .first()
                .extracting(PedidoHistorico::getTipoEvento)
                .isEqualTo(PedidoHistorico.TipoEvento.CRIADO);
    }

    @Test
    void deveFinalizarPedidoERegistrarHistorico() {
        limparDados();

        Pedido pedido = pedidoService.criar("Refrigerante");
        Pedido finalizado = pedidoService.finalizar(pedido.getId());

        assertThat(finalizado.isFinalizado()).isTrue();
        assertThat(pedidoHistoricoRepository.findByPedidoIdOrderByAlteradoEmAsc(pedido.getId()))
                .extracting(PedidoHistorico::getTipoEvento)
                .containsExactly(PedidoHistorico.TipoEvento.CRIADO, PedidoHistorico.TipoEvento.FINALIZADO);
    }

    @Test
    void deveLancarExcecaoQuandoPedidoNaoExistir() {
        limparDados();

        assertThrows(RuntimeException.class, () -> pedidoService.finalizar(999L));
    }

    private void limparDados() {
        pedidoHistoricoRepository.deleteAll();
        pedidoRepository.deleteAll();
    }
}