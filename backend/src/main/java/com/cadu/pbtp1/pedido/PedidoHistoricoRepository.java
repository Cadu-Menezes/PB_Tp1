package com.cadu.pbtp1.pedido;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoHistoricoRepository extends JpaRepository<PedidoHistorico, Long> {

    List<PedidoHistorico> findByPedidoIdOrderByAlteradoEmAsc(Long pedidoId);
}