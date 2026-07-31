package com.cadu.pbtp1.pedido;

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
@Table(name = "pedido_historico")
public class PedidoHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long pedidoId;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private boolean finalizado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEvento tipoEvento;

    @Column(nullable = false)
    private LocalDateTime alteradoEm;

    protected PedidoHistorico() {
    }

    public PedidoHistorico(Long pedidoId, String descricao, boolean finalizado, TipoEvento tipoEvento, LocalDateTime alteradoEm) {
        this.pedidoId = pedidoId;
        this.descricao = descricao;
        this.finalizado = finalizado;
        this.tipoEvento = tipoEvento;
        this.alteradoEm = alteradoEm;
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

    public boolean isFinalizado() {
        return finalizado;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }

    public LocalDateTime getAlteradoEm() {
        return alteradoEm;
    }

    public enum TipoEvento {
        CRIADO,
        FINALIZADO
    }
}