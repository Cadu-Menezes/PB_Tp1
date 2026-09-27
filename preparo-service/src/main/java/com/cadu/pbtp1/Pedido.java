package com.cadu.pbtp1;


import java.util.ArrayList;
import java.util.List;

public class Pedido {

    private final Long id;
    private final Endereco enderecoEntrega;
    private final List<String> itens = new ArrayList<>();

    public Pedido(Long id, Endereco enderecoEntrega) {
        this.id = id;
        this.enderecoEntrega = enderecoEntrega;
    }

    public void adicionarItem(String item) {
        if (item == null || item.isBlank()) {
            throw new IllegalArgumentException("O item não pode ser vazio");
        }

        itens.add(item);
    }

    public Long getId() {
        return id;
    }

    public Endereco getEnderecoEntrega() {
        return enderecoEntrega;
    }

    public List<String> getItens() {
        return List.copyOf(itens);
    }
}