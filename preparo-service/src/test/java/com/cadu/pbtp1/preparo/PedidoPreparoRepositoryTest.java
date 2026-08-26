package com.cadu.pbtp1.preparo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class PedidoPreparoRepositoryTest {

    @Autowired
    private PedidoPreparoRepository pedidoPreparoRepository;

    @Test
    void deveSalvarEBuscarPedidoPreparo() {
        PedidoPreparo pedidoPreparo = new PedidoPreparo(1L, "2 hambúrgueres", StatusPreparo.RECEBIDO);

        PedidoPreparo salvo = pedidoPreparoRepository.save(pedidoPreparo);

        assertThat(salvo.getId()).isNotNull();
        assertThat(pedidoPreparoRepository.findByPedidoId(1L)).isPresent();
    }
}