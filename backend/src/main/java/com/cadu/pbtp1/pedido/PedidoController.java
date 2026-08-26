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

import com.cadu.pbtp1.preparo.AtualizarStatusPreparoRequest;
import com.cadu.pbtp1.preparo.PedidoPreparoResposta;
import com.cadu.pbtp1.preparo.PedidoPreparoService;
import com.cadu.pbtp1.preparo.StatusPreparo;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final PedidoPreparoService pedidoPreparoService;

    public PedidoController(PedidoService pedidoService, PedidoPreparoService pedidoPreparoService) {
        this.pedidoService = pedidoService;
        this.pedidoPreparoService = pedidoPreparoService;
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

    @GetMapping("/{id}/preparo")
    public PedidoPreparoResposta obterPreparo(@PathVariable Long id) {
        return pedidoPreparoService.obter(id);
    }

    @PatchMapping("/{id}/preparo")
    public PedidoPreparoResposta atualizarPreparo(@PathVariable Long id, @Valid @RequestBody AtualizarStatusPreparoRequest request) {
        return pedidoPreparoService.atualizarStatus(id, request.status());
    }

    @GetMapping("/preparos")
    public List<PedidoPreparoResposta> listarPreparos() {
        return pedidoPreparoService.listar();
    }

    @GetMapping("/{id}/historico")
    public List<PedidoHistoricoResposta> historico(@PathVariable Long id) {
        return pedidoService.listarHistorico(id).stream().map(PedidoHistoricoResposta::new).toList();
    }

    public record NovoPedidoRequest(@NotBlank(message = "A descrição é obrigatória.") String descricao) {
    }

    public record PedidoResposta(Long id, String descricao, boolean finalizado) {
        public PedidoResposta(Pedido pedido) {
            this(pedido.getId(), pedido.getDescricao(), pedido.isFinalizado());
        }
    }

    public record PedidoHistoricoResposta(Long id, Long pedidoId, String descricao, boolean finalizado, String tipoEvento, String alteradoEm) {
        public PedidoHistoricoResposta(PedidoHistorico historico) {
            this(
                    historico.getId(),
                    historico.getPedidoId(),
                    historico.getDescricao(),
                    historico.isFinalizado(),
                    historico.getTipoEvento().name(),
                    historico.getAlteradoEm().toString()
            );
        }
    }
}
