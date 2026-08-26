package com.cadu.pbtp1.preparo;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pedidos_preparo")
public class PedidoPreparo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long pedidoId;

    @Column(nullable = false)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPreparo status;

    @Column(nullable = false)
    private LocalDateTime atualizadoEm;

    protected PedidoPreparo() {
    }

    public PedidoPreparo(Long pedidoId, String descricao, StatusPreparo status) {
        this.pedidoId = pedidoId;
        this.descricao = descricao;
        this.status = status;
        this.atualizadoEm = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public String getDescricao() {
        return descricao;
    }

    public StatusPreparo getStatus() {
        return status;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void atualizarDescricao(String descricao) {
        this.descricao = descricao;
        this.atualizadoEm = LocalDateTime.now();
    }

    public void atualizarStatus(StatusPreparo status) {
        this.status = status;
        this.atualizadoEm = LocalDateTime.now();
    }
}