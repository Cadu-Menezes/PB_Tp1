package com.faculdade.produtos.model;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/*
    Testes baseados em propriedades para o domínio Produto usando Jqwik.
    Verifica propriedades que devem ser sempre verdadeiras independente da entrada.
*/
@DisplayName("Testes Baseados em Propriedades do Domínio Produto")
class ProdutoDomainProperties {

    @Property
    @DisplayName("Produto criado deve sempre ter ID único")
    void produtoCriadoDeveSempreTerIdUnico() {
        // When
        Produto produto1 = criarProdutoAleatorio();
        Produto produto2 = criarProdutoAleatorio();
        
        // Then
        assertNotEquals(produto1.getId(), produto2.getId());
    }

    @Property
    @DisplayName("Produto atualizado deve manter mesmo ID")
    void produtoAtualizadoDeveMaterMesmoId(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String nomeOriginal,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String nomeAtualizado) {
        // Given
        Produto produtoOriginal = criarProdutoComNome(nomeOriginal);
        Nome novoNome = Nome.of(nomeAtualizado);
        
        // When
        Produto produtoAtualizado = produtoOriginal.atualizar(
                novoNome,
                produtoOriginal.getDescricao(),
                produtoOriginal.getPreco(),
                produtoOriginal.getCategoria(),
                produtoOriginal.getQuantidadeEstoque()
        );
        
        // Then
        assertEquals(produtoOriginal.getId(), produtoAtualizado.getId());
    }

    @Property
    @DisplayName("Produto atualizado deve ter data de atualização >= data de criação")
    void produtoAtualizadoDeveTerDataAtualizacaoMaiorIgualDataCriacao(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String nomeOriginal,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String nomeAtualizado) {
        // Given
        Produto produtoOriginal = criarProdutoComNome(nomeOriginal);
        Nome novoNome = Nome.of(nomeAtualizado);
        
        // When
        Produto produtoAtualizado = produtoOriginal.atualizar(
                novoNome,
                produtoOriginal.getDescricao(),
                produtoOriginal.getPreco(),
                produtoOriginal.getCategoria(),
                produtoOriginal.getQuantidadeEstoque()
        );
        
        // Then
        assertFalse(produtoAtualizado.getAtualizadoEm().isBefore(produtoAtualizado.getCriadoEm()));
    }

    @Property
    @DisplayName("Atualização de estoque deve manter outros atributos inalterados")
    void atualizacaoEstoqueDeveManterOutrosAtributosInalterados(
            @ForAll @IntRange(min = 0, max = 1000) int estoqueOriginal,
            @ForAll @IntRange(min = 0, max = 1000) int novoEstoque) {
        // Given
        Produto produtoOriginal = criarProdutoComEstoque(estoqueOriginal);
        QuantidadeEstoque novaQuantidade = QuantidadeEstoque.of(novoEstoque);
        
        // When
        Produto produtoAtualizado = produtoOriginal.atualizarEstoque(novaQuantidade);
        
        // Then
        assertEquals(produtoOriginal.getId(), produtoAtualizado.getId());
        assertEquals(produtoOriginal.getNome(), produtoAtualizado.getNome());
        assertEquals(produtoOriginal.getDescricao(), produtoAtualizado.getDescricao());
        assertEquals(produtoOriginal.getPreco(), produtoAtualizado.getPreco());
        assertEquals(produtoOriginal.getCategoria(), produtoAtualizado.getCategoria());
        assertEquals(produtoOriginal.getCriadoEm(), produtoAtualizado.getCriadoEm());
        assertEquals(novaQuantidade, produtoAtualizado.getQuantidadeEstoque());
    }

    @Property
    @DisplayName("Equals deve ser baseado apenas no ID")
    void equalsDeveSerBaseadoApenasNoId(@ForAll @StringLength(min = 1, max = 50) @AlphaChars String nome) {
        // Given
        ProdutoId id = ProdutoId.gerar();
        LocalDateTime agora = LocalDateTime.now();
        
        Produto produto1 = Produto.builder()
                .id(id)
                .nome(Nome.of(nome))
                .descricao(Descricao.of("Descrição 1"))
                .preco(Preco.of("10.00"))
                .categoria(Categoria.ELETRONICOS)
                .quantidadeEstoque(QuantidadeEstoque.of(1))
                .criadoEm(agora)
                .atualizadoEm(agora)
                .build();
        
        Produto produto2 = Produto.builder()
                .id(id) // Mesmo ID
                .nome(Nome.of("Nome Completamente Diferente"))
                .descricao(Descricao.of("Descrição Totalmente Diferente"))
                .preco(Preco.of("999.99"))
                .categoria(Categoria.LIVROS)
                .quantidadeEstoque(QuantidadeEstoque.of(999))
                .criadoEm(agora.minusHours(1))
                .atualizadoEm(agora.plusHours(1))
                .build();
        
        // When/Then
        assertEquals(produto1, produto2);
        assertEquals(produto1.hashCode(), produto2.hashCode());
    }

    @Property
    @DisplayName("ToString deve sempre conter informações essenciais")
    void toStringDeveSempreConterInformacoesEssenciais(@ForAll @StringLength(min = 1, max = 50) @AlphaChars String nome) {
        // Given
        Produto produto = criarProdutoComNome(nome);
        
        // When
        String toString = produto.toString();
        
        // Then
        assertTrue(toString.contains("Produto"));
        assertTrue(toString.contains(produto.getId().toString()));
        assertTrue(toString.contains(produto.getNome().getValue()));
        assertTrue(toString.contains(produto.getPreco().toString()));
    }

    @Property
    @DisplayName("Produto deve ser imutável - operações devem retornar nova instância")
    void produtoDeveSerImutavel(@ForAll @StringLength(min = 1, max = 50) @AlphaChars String nomeAtualizado) {
        // Given
        Produto produtoOriginal = criarProdutoAleatorio();
        Nome novoNome = Nome.of(nomeAtualizado);
        
        // When
        Produto produtoAtualizado = produtoOriginal.atualizar(
                novoNome,
                produtoOriginal.getDescricao(),
                produtoOriginal.getPreco(),
                produtoOriginal.getCategoria(),
                produtoOriginal.getQuantidadeEstoque()
        );
        
        // Then
        assertNotSame(produtoOriginal, produtoAtualizado);
        // Produto original não deve ter sido alterado
        assertNotEquals(novoNome, produtoOriginal.getNome());
    }

    // Métodos auxiliares para geração de dados de teste

    private Produto criarProdutoAleatorio() {
        return Produto.criar(
                Nome.of("Produto " + System.nanoTime()),
                Descricao.of("Descrição padrão"),
                Preco.of("99.99"),
                Categoria.ELETRONICOS,
                QuantidadeEstoque.of(10)
        );
    }

    private Produto criarProdutoComNome(String nome) {
        return Produto.criar(
                Nome.of(nome),
                Descricao.of("Descrição padrão"),
                Preco.of("99.99"),
                Categoria.ELETRONICOS,
                QuantidadeEstoque.of(10)
        );
    }

    private Produto criarProdutoComEstoque(int estoque) {
        return Produto.criar(
                Nome.of("Produto Teste"),
                Descricao.of("Descrição padrão"),
                Preco.of("99.99"),
                Categoria.ELETRONICOS,
                QuantidadeEstoque.of(estoque)
        );
    }
}