package com.cadu.pbtp1.preparo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PedidoPreparoServiceTest {

    @Autowired
    private PedidoPreparoService pedidoPreparoService;

    @Autowired
    private PedidoPreparoRepository pedidoPreparoRepository;

    @Test
    void deveRegistrarPedidoComStatusRecebido() {
        pedidoPreparoRepository.deleteAll();

        PedidoPreparo pedidoPreparo = pedidoPreparoService.registrarPedido(10L, "Combo da casa");

        assertThat(pedidoPreparo.getStatus()).isEqualTo(StatusPreparo.RECEBIDO);
        assertThat(pedidoPreparoRepository.findByPedidoId(10L)).isPresent();
    }

    @Test
    void deveAtualizarStatusDoPedido() {
        pedidoPreparoRepository.deleteAll();
        pedidoPreparoService.registrarPedido(11L, "Combo especial");

        PedidoPreparo atualizado = pedidoPreparoService.atualizarStatus(11L, StatusPreparo.PRONTO);

        assertThat(atualizado.getStatus()).isEqualTo(StatusPreparo.PRONTO);
    }
}