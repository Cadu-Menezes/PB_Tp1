package com.cadu.pbtp1.preparo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NovoPedidoPreparoRequest(@NotNull Long pedidoId, @NotBlank String descricao) {
}