package com.cadu.pbtp1.preparo;

import jakarta.validation.constraints.NotNull;

public record AtualizarStatusPreparoRequest(@NotNull StatusPreparo status) {
}