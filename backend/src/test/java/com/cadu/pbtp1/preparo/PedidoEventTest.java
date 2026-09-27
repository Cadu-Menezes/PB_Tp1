package com.cadu.pbtp1.preparo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PedidoEventTest {

    @Test
    void deveCriarEventoDePedidoCriado() {
        PedidoEvent evento = PedidoEvent.criado(10L, "Combo da casa");

        assertThat(evento.eventId()).isNotBlank();
        assertThat(evento.eventType()).isEqualTo("pedido.criado");
        assertThat(evento.pedidoId()).isEqualTo(10L);
        assertThat(evento.status()).isEqualTo(StatusPreparo.RECEBIDO);
        assertThat(evento.descricao()).isEqualTo("Combo da casa");
    }

    @Test
    void deveCriarEventoDePedidoFinalizado() {
        PedidoEvent evento = PedidoEvent.finalizado(11L);

        assertThat(evento.eventType()).isEqualTo("pedido.finalizado");
        assertThat(evento.pedidoId()).isEqualTo(11L);
        assertThat(evento.status()).isEqualTo(StatusPreparo.ENTREGUE);
    }

    @Test
    void deveCriarEventoDeStatusAtualizado() {
        PedidoEvent evento = PedidoEvent.statusAtualizado(12L, StatusPreparo.PRONTO);

        assertThat(evento.eventType()).isEqualTo("preparo.status-atualizado");
        assertThat(evento.pedidoId()).isEqualTo(12L);
        assertThat(evento.status()).isEqualTo(StatusPreparo.PRONTO);
    }
}
