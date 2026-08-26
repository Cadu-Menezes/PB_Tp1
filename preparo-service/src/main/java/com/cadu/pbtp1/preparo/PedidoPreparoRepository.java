package com.cadu.pbtp1.preparo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoPreparoRepository extends JpaRepository<PedidoPreparo, Long> {

    Optional<PedidoPreparo> findByPedidoId(Long pedidoId);

    List<PedidoPreparo> findAllByOrderByAtualizadoEmDesc();
}