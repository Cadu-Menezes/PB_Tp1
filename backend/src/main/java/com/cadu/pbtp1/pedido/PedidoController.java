package com.cadu.pbtp1.pedido;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public List<PedidoResposta> listar() {
        return pedidoService.listar().stream().map(PedidoResposta::new).toList();
    }

    @PostMapping
    public PedidoResposta criar(@Valid @RequestBody NovoPedidoRequest request) {
        return new PedidoResposta(pedidoService.criar(request.descricao()));
    }

    @PatchMapping("/{id}/finalizar")
    public PedidoResposta finalizar(@PathVariable Long id) {
        return new PedidoResposta(pedidoService.finalizar(id));
    }

    public record NovoPedidoRequest(@NotBlank(message = "A descrição é obrigatória.") String descricao) {
    }

    public record PedidoResposta(Long id, String descricao, boolean finalizado) {
        public PedidoResposta(Pedido pedido) {
            this(pedido.getId(), pedido.getDescricao(), pedido.isFinalizado());
        }
    }
}
