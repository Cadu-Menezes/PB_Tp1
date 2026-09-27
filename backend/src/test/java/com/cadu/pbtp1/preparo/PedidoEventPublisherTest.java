package com.cadu.pbtp1.preparo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.cadu.pbtp1.PbTp1Application;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

class PedidoEventPublisherTest {

    private final RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private final PedidoEventPublisher publisher = new PedidoEventPublisher(rabbitTemplate, objectMapper);

    @Test
    void devePublicarPedidoCriadoComRoutingKeyECorpoPadronizado() throws Exception {
        publisher.publicarPedidoCriado(10L, "Combo da casa");

        ArgumentCaptor<String> corpo = ArgumentCaptor.forClass(String.class);
        verify(rabbitTemplate).convertAndSend(eq(PbTp1Application.PEDIDOS_EXCHANGE), eq("pedido.criado"), corpo.capture());

        assertThat(corpo.getValue()).contains("\"eventType\":\"pedido.criado\"");
        assertThat(corpo.getValue()).contains("\"pedidoId\":10");
        assertThat(corpo.getValue()).contains("\"status\":\"RECEBIDO\"");
    }

    @Test
    void devePublicarPedidoFinalizadoEStatusAtualizadoComSuasRoutingKeys() {
        publisher.publicarPedidoFinalizado(11L);
        publisher.publicarStatusAtualizado(12L, StatusPreparo.PRONTO);

        verify(rabbitTemplate).convertAndSend(eq(PbTp1Application.PEDIDOS_EXCHANGE), eq("pedido.finalizado"), org.mockito.ArgumentMatchers.anyString());
        verify(rabbitTemplate).convertAndSend(eq(PbTp1Application.PEDIDOS_EXCHANGE), eq("preparo.status-atualizado"), org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void deveConverterFalhaDeSerializacaoEmExcecaoDaAplicacao() throws Exception {
        ObjectMapper mapper = mock(ObjectMapper.class);
        org.mockito.Mockito.when(mapper.writeValueAsString(org.mockito.ArgumentMatchers.any()))
                .thenThrow(new JsonProcessingException("falha de teste") {
                });
        PedidoEventPublisher publisherComFalha = new PedidoEventPublisher(rabbitTemplate, mapper);

        assertThatThrownBy(() -> publisherComFalha.publicarPedidoCriado(13L, "Pedido inválido"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Não foi possível serializar o evento do pedido.");
    }
}
