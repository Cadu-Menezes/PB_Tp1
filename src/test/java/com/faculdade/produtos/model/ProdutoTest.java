package com.faculdade.produtos.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/*
    Testes para a classe Produto.
    Cobre criação, atualização, imutabilidade e builder pattern.
*/
@DisplayName("Testes da Entidade Produto")
class ProdutoTest {

    private Nome nomeValido;
    private Descricao descricaoValida;
    private Preco precoValido;
    private Categoria categoriaValida;
    private QuantidadeEstoque estoqueValido;

    @BeforeEach
    void setUp() {
        nomeValido = Nome.of("Produto Teste");
        descricaoValida = Descricao.of("Descrição do produto teste");
        precoValido = Preco.of("99.90");
        categoriaValida = Categoria.ELETRONICOS;
        estoqueValido = QuantidadeEstoque.of(10);
    }

    @Test
    @DisplayName("Deve criar produto válido usando método criar")
    void deveCriarProdutoValidoUsandoMetodoCriar() {
        // When
        Produto produto = Produto.criar(nomeValido, descricaoValida, precoValido, categoriaValida, estoqueValido);
        
        // Then
        assertNotNull(produto);
        assertNotNull(produto.getId());
        assertEquals(nomeValido, produto.getNome());
        assertEquals(descricaoValida, produto.getDescricao());
        assertEquals(precoValido, produto.getPreco());
        assertEquals(categoriaValida, produto.getCategoria());
        assertEquals(estoqueValido, produto.getQuantidadeEstoque());
        assertNotNull(produto.getCriadoEm());
        assertNotNull(produto.getAtualizadoEm());
        assertEquals(produto.getCriadoEm(), produto.getAtualizadoEm());
    }

    @Test
    @DisplayName("Deve criar produto usando builder")
    void deveCriarProdutoUsandoBuilder() {
        // Given
        ProdutoId produtoId = ProdutoId.gerar();
        LocalDateTime agora = LocalDateTime.now();
        
        // When
        Produto produto = Produto.builder()
                .id(produtoId)
                .nome(nomeValido)
                .descricao(descricaoValida)
                .preco(precoValido)
                .categoria(categoriaValida)
                .quantidadeEstoque(estoqueValido)
                .criadoEm(agora)
                .atualizadoEm(agora)
                .build();
        
        // Then
        assertEquals(produtoId, produto.getId());
        assertEquals(nomeValido, produto.getNome());
        assertEquals(agora, produto.getCriadoEm());
    }

    @Test
    @DisplayName("Deve rejeitar criação com campos obrigatórios nulos via builder")
    void deveRejeitarCriacaoComCamposObrigatoriosNulosViaBuilder() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> {
            Produto.builder()
                    .nome(nomeValido)
                    .descricao(descricaoValida)
                    .preco(precoValido)
                    .categoria(categoriaValida)
                    .quantidadeEstoque(estoqueValido)
                    // Sem ID
                    .criadoEm(LocalDateTime.now())
                    .atualizadoEm(LocalDateTime.now())
                    .build();
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            Produto.builder()
                    .id(ProdutoId.gerar())
                    // Sem nome
                    .descricao(descricaoValida)
                    .preco(precoValido)
                    .categoria(categoriaValida)
                    .quantidadeEstoque(estoqueValido)
                    .criadoEm(LocalDateTime.now())
                    .atualizadoEm(LocalDateTime.now())
                    .build();
        });
    }

    @Test
    @DisplayName("Deve atualizar produto mantendo imutabilidade")
    void deveAtualizarProdutoMantendoImutabilidade() {
        // Given
        Produto produtoOriginal = Produto.criar(nomeValido, descricaoValida, precoValido, categoriaValida, estoqueValido);
        
        Nome novoNome = Nome.of("Produto Atualizado");
        Preco novoPreco = Preco.of("199.90");
        
        // When
        Produto produtoAtualizado = produtoOriginal.atualizar(novoNome, descricaoValida, novoPreco, categoriaValida, estoqueValido);
        
        // Then
        // Produto original não deve ter mudado
        assertEquals(nomeValido, produtoOriginal.getNome());
        assertEquals(precoValido, produtoOriginal.getPreco());
        
        // Produto atualizado deve ter novos valores
        assertEquals(novoNome, produtoAtualizado.getNome());
        assertEquals(novoPreco, produtoAtualizado.getPreco());
        assertEquals(produtoOriginal.getId(), produtoAtualizado.getId()); // ID permanece igual
        assertEquals(produtoOriginal.getCriadoEm(), produtoAtualizado.getCriadoEm()); // Data de criação permanece
        assertTrue(produtoAtualizado.getAtualizadoEm().isAfter(produtoOriginal.getAtualizadoEm()) || 
                   produtoAtualizado.getAtualizadoEm().equals(produtoOriginal.getAtualizadoEm()));
    }

    @Test
    @DisplayName("Deve atualizar estoque mantendo imutabilidade")
    void deveAtualizarEstoqueMantendoImutabilidade() {
        // Given
        Produto produtoOriginal = Produto.criar(nomeValido, descricaoValida, precoValido, categoriaValida, estoqueValido);
        QuantidadeEstoque novoEstoque = QuantidadeEstoque.of(5);
        
        // When
        Produto produtoAtualizado = produtoOriginal.atualizarEstoque(novoEstoque);
        
        // Then
        assertEquals(estoqueValido, produtoOriginal.getQuantidadeEstoque()); // Original inalterado
        assertEquals(novoEstoque, produtoAtualizado.getQuantidadeEstoque()); // Novo com estoque atualizado
        assertEquals(produtoOriginal.getId(), produtoAtualizado.getId());
        assertEquals(produtoOriginal.getNome(), produtoAtualizado.getNome());
    }

    @Test
    @DisplayName("Deve implementar equals baseado no ID")
    void deveImplementarEqualsBaseadoNoId() {
        // Given
        ProdutoId id1 = ProdutoId.gerar();
        ProdutoId id2 = ProdutoId.gerar();
        
        LocalDateTime agora = LocalDateTime.now();
        
        Produto produto1a = Produto.builder()
                .id(id1)
                .nome(nomeValido)
                .descricao(descricaoValida)
                .preco(precoValido)
                .categoria(categoriaValida)
                .quantidadeEstoque(estoqueValido)
                .criadoEm(agora)
                .atualizadoEm(agora)
                .build();
                
        Produto produto1b = Produto.builder()
                .id(id1) // Mesmo ID
                .nome(Nome.of("Nome Diferente"))
                .descricao(Descricao.of("Descrição Diferente"))
                .preco(Preco.of("999.99"))
                .categoria(Categoria.LIVROS)
                .quantidadeEstoque(QuantidadeEstoque.of(100))
                .criadoEm(agora)
                .atualizadoEm(agora)
                .build();
                
        Produto produto2 = Produto.builder()
                .id(id2) // ID diferente
                .nome(nomeValido)
                .descricao(descricaoValida)
                .preco(precoValido)
                .categoria(categoriaValida)
                .quantidadeEstoque(estoqueValido)
                .criadoEm(agora)
                .atualizadoEm(agora)
                .build();
        
        // When/Then
        assertEquals(produto1a, produto1b); // Mesmo ID = iguais
        assertNotEquals(produto1a, produto2); // IDs diferentes = diferentes
        assertEquals(produto1a, produto1a); // Reflexivo
    }

    @Test
    @DisplayName("Deve implementar hashCode consistente com equals")
    void deveImplementarHashCodeConsistenteComEquals() {
        // Given
        ProdutoId id = ProdutoId.gerar();
        LocalDateTime agora = LocalDateTime.now();
        
        Produto produto1 = Produto.builder()
                .id(id)
                .nome(nomeValido)
                .descricao(descricaoValida)
                .preco(precoValido)
                .categoria(categoriaValida)
                .quantidadeEstoque(estoqueValido)
                .criadoEm(agora)
                .atualizadoEm(agora)
                .build();
                
        Produto produto2 = Produto.builder()
                .id(id) // Mesmo ID
                .nome(Nome.of("Nome Diferente"))
                .descricao(descricaoValida)
                .preco(precoValido)
                .categoria(categoriaValida)
                .quantidadeEstoque(estoqueValido)
                .criadoEm(agora)
                .atualizadoEm(agora)
                .build();
        
        // When/Then
        assertEquals(produto1.hashCode(), produto2.hashCode());
    }

    @Test
    @DisplayName("Deve implementar toString com informações relevantes")
    void deveImplementarToStringComInformacoesRelevantes() {
        // Given
        Produto produto = Produto.criar(nomeValido, descricaoValida, precoValido, categoriaValida, estoqueValido);
        
        // When
        String toString = produto.toString();
        
        // Then
        assertTrue(toString.contains("Produto"));
        assertTrue(toString.contains(produto.getId().toString()));
        assertTrue(toString.contains(nomeValido.getValue()));
        assertTrue(toString.contains(precoValido.toString()));
    }

    @Test
    @DisplayName("Deve ter getters que retornam objetos imutáveis")
    void deveTerGettersQueRetornamObjetosImutaveis() {
        // Given
        Produto produto = Produto.criar(nomeValido, descricaoValida, precoValido, categoriaValida, estoqueValido);
        
        // When/Then
        assertNotNull(produto.getId());
        assertNotNull(produto.getNome());
        assertNotNull(produto.getDescricao());
        assertNotNull(produto.getPreco());
        assertNotNull(produto.getCategoria());
        assertNotNull(produto.getQuantidadeEstoque());
        assertNotNull(produto.getCriadoEm());
        assertNotNull(produto.getAtualizadoEm());
        
        // Verifica que são os mesmos objetos (referência)
        assertSame(nomeValido, produto.getNome());
        assertSame(descricaoValida, produto.getDescricao());
        assertSame(precoValido, produto.getPreco());
        assertSame(categoriaValida, produto.getCategoria());
        assertSame(estoqueValido, produto.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("Deve garantir que datas de criação e atualização são definidas automaticamente")
    void deveGarantirQueDatasDeCriacaoEAtualizacaoSaoDefinidasAutomaticamente() {
        // Given
        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);
        
        // When
        Produto produto = Produto.criar(nomeValido, descricaoValida, precoValido, categoriaValida, estoqueValido);
        
        // Then
        LocalDateTime depois = LocalDateTime.now().plusSeconds(1);
        
        assertTrue(produto.getCriadoEm().isAfter(antes));
        assertTrue(produto.getCriadoEm().isBefore(depois));
        assertTrue(produto.getAtualizadoEm().isAfter(antes));
        assertTrue(produto.getAtualizadoEm().isBefore(depois));
    }
}