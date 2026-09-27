package com.cadu.pbtp1.preparo;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class PedidoEventListenerTest {

    private final PedidoPreparoService pedidoPreparoService = mock(PedidoPreparoService.class);
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private final PedidoEventListener listener = new PedidoEventListener(pedidoPreparoService, objectMapper);

    @Test
    void deveRegistrarPedidoQuandoReceberEventoDeCriacao() throws Exception {
        PedidoEvent evento = new PedidoEvent("id-criado", "pedido.criado", Instant.now(), 20L,
            "Combo especial", StatusPreparo.RECEBIDO);

        listener.receber(objectMapper.writeValueAsString(evento));

        verify(pedidoPreparoService).registrarPedido(20L, "Combo especial");
    }

    @Test
    void deveAtualizarStatusQuandoReceberFinalizacaoOuAtualizacao() throws Exception {
        listener.receber(objectMapper.writeValueAsString(new PedidoEvent("id-finalizado", "pedido.finalizado",
            Instant.now(), 21L, null, StatusPreparo.ENTREGUE)));
        listener.receber(objectMapper.writeValueAsString(new PedidoEvent("id-status", "preparo.status-atualizado",
            Instant.now(), 22L, null, StatusPreparo.PRONTO)));

        verify(pedidoPreparoService).atualizarStatus(21L, StatusPreparo.ENTREGUE);
        verify(pedidoPreparoService).atualizarStatus(22L, StatusPreparo.PRONTO);
    }

    @Test
    void deveRejeitarJsonInvalido() {
        assertThatThrownBy(() -> listener.receber("{\"eventType\":\"pedido.criado\""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Evento de pedido inválido.");
    }

    @Test
    void deveRejeitarTipoDeEventoDesconhecido() throws Exception {
        PedidoEvent evento = new PedidoEvent("id", "pedido.desconhecido", java.time.Instant.now(), 23L,
                null, null);

        assertThatThrownBy(() -> listener.receber(objectMapper.writeValueAsString(evento)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Tipo de evento não suportado: pedido.desconhecido");
    }
}
