package com.cadu.pbtp1.preparo;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/preparos")
public class PedidoPreparoController {

    private final PedidoPreparoService pedidoPreparoService;

    public PedidoPreparoController(PedidoPreparoService pedidoPreparoService) {
        this.pedidoPreparoService = pedidoPreparoService;
    }

    @GetMapping
    public List<PedidoPreparoResposta> listar() {
        return pedidoPreparoService.listar().stream().map(PedidoPreparoResposta::new).toList();
    }

    @GetMapping("/{pedidoId}")
    public PedidoPreparoResposta obter(@PathVariable Long pedidoId) {
        return new PedidoPreparoResposta(pedidoPreparoService.obter(pedidoId));
    }

    @PostMapping
    public PedidoPreparoResposta registrar(@Valid @RequestBody NovoPedidoPreparoRequest request) {
        return new PedidoPreparoResposta(pedidoPreparoService.registrarPedido(request.pedidoId(), request.descricao()));
    }

    @PatchMapping("/{pedidoId}")
    public PedidoPreparoResposta atualizar(@PathVariable Long pedidoId, @Valid @RequestBody AtualizarStatusPreparoRequest request) {
        return new PedidoPreparoResposta(pedidoPreparoService.atualizarStatus(pedidoId, request.status()));
    }
}