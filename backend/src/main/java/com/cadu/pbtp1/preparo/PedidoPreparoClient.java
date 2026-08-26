package com.cadu.pbtp1.preparo;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "pedidoPreparoClient", url = "${preparo.service.url}", path = "/api/preparos")
public interface PedidoPreparoClient {

    @GetMapping
    List<PedidoPreparoResposta> listar();

    @GetMapping("/{pedidoId}")
    PedidoPreparoResposta obterPorPedidoId(@PathVariable Long pedidoId);

    @PostMapping
    PedidoPreparoResposta registrar(@RequestBody NovoPedidoPreparoRequest request);

    @PatchMapping("/{pedidoId}")
    PedidoPreparoResposta atualizarStatus(@PathVariable Long pedidoId, @RequestBody AtualizarStatusPreparoRequest request);
}