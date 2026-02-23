package com.faculdade.produtos.service;

import com.faculdade.produtos.config.DatabaseConfig;
import com.faculdade.produtos.config.DatabaseMigration;
import com.faculdade.produtos.exception.*;
import com.faculdade.produtos.model.*;
import com.faculdade.produtos.repository.impl.RepositorioPostgreSqlProduto;

import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
    Testes de integração para ProdutoService.
    Usa banco H2 em memória para isolamento dos testes.
*/
@DisplayName("Testes de Integração do ProdutoService")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProdutoServiceIntegrationTest {

    private static DataSource dataSource;
    private static RepositorioPostgreSqlProduto repositorio;
    private static ProdutoService produtoService;

    @BeforeAll
    static void configurarTudo() throws Exception {
        // Configurar banco de teste
        dataSource = DatabaseConfig.createTestDataSource();
        DatabaseMigration.migrate(dataSource);
        
        // Configurar dependências
        repositorio = new RepositorioPostgreSqlProduto(dataSource);
        produtoService = new ProdutoService(repositorio, repositorio);
    }

    @BeforeEach
    void configurar() throws Exception {
        // Limpar dados antes de cada teste
        DatabaseMigration.dropAllTables(dataSource);
        DatabaseMigration.migrate(dataSource);
    }

    @Test
    @Order(1)
    @DisplayName("Deve criar produto com sucesso")
    void deveCriarProdutoComSucesso() throws Exception {
        // Given
        Nome nome = Nome.of("Smartphone Premium");
        Descricao descricao = Descricao.of("Smartphone top de linha com 256GB");
        Preco preco = Preco.of("2499.90");
        Categoria categoria = Categoria.ELETRONICOS;
        QuantidadeEstoque quantidadeEstoque = QuantidadeEstoque.of(50);

        // When
        ProdutoId produtoId = produtoService.criarProduto(nome, descricao, preco, categoria, quantidadeEstoque);

        // Then
        assertNotNull(produtoId);
        
        Produto produtoSalvo = produtoService.obterProduto(produtoId);
        assertEquals(nome, produtoSalvo.getNome());
        assertEquals(descricao, produtoSalvo.getDescricao());
        assertEquals(preco, produtoSalvo.getPreco());
        assertEquals(categoria, produtoSalvo.getCategoria());
        assertEquals(quantidadeEstoque, produtoSalvo.getQuantidadeEstoque());
        assertNotNull(produtoSalvo.getCriadoEm());
        assertNotNull(produtoSalvo.getAtualizadoEm());
    }

    @Test
    @Order(2)
    @DisplayName("Deve rejeitar criação de produto com nome duplicado")
    void deveRejeitarCriacaoDeProdutoComNomeDuplicado() throws Exception {
        // Given
        Nome nomeDuplicado = Nome.of("Produto Duplicado");
        Descricao descricao = Descricao.of("Descrição do produto");
        Preco preco = Preco.of("100.00");
        Categoria categoria = Categoria.OUTROS;
        QuantidadeEstoque estoque = QuantidadeEstoque.of(10);

        // Criar primeiro produto
        produtoService.criarProduto(nomeDuplicado, descricao, preco, categoria, estoque);

        // When/Then
        assertThrows(ProdutoJaExisteException.class, () -> {
            produtoService.criarProduto(nomeDuplicado, descricao, preco, categoria, estoque);
        });
    }

    @Test
    @Order(3)
    @DisplayName("Deve atualizar produto com sucesso")
    void deveAtualizarProdutoComSucesso() throws Exception {
        // Given
        Nome nomeOriginal = Nome.of("Produto Original");
        Descricao descricaoOriginal = Descricao.of("Descrição original");
        Preco precoOriginal = Preco.of("100.00");
        
        ProdutoId produtoId = produtoService.criarProduto(
            nomeOriginal, descricaoOriginal, precoOriginal, 
            Categoria.OUTROS, QuantidadeEstoque.of(10));

        Nome novoNome = Nome.of("Produto Atualizado");
        Descricao novaDescricao = Descricao.of("Descrição atualizada");
        Preco novoPreco = Preco.of("150.00");

        // When
        produtoService.atualizarProduto(
            produtoId, novoNome, novaDescricao, novoPreco, 
            Categoria.ELETRONICOS, QuantidadeEstoque.of(20));

        // Then
        Produto produtoAtualizado = produtoService.obterProduto(produtoId);
        assertEquals(novoNome, produtoAtualizado.getNome());
        assertEquals(novaDescricao, produtoAtualizado.getDescricao());
        assertEquals(novoPreco, produtoAtualizado.getPreco());
        assertEquals(Categoria.ELETRONICOS, produtoAtualizado.getCategoria());
        assertEquals(QuantidadeEstoque.of(20), produtoAtualizado.getQuantidadeEstoque());
    }

    @Test
    @Order(4)
    @DisplayName("Deve rejeitar atualização para nome já existente")
    void deveRejeitarAtualizacaoParaNomeJaExistente() throws Exception {
        // Given
        Nome nome1 = Nome.of("Produto 1");
        Nome nome2 = Nome.of("Produto 2");
        
        ProdutoId produto1Id = produtoService.criarProduto(
            nome1, Descricao.of("Desc 1"), Preco.of("100.00"), 
            Categoria.OUTROS, QuantidadeEstoque.of(10));
            
        ProdutoId produto2Id = produtoService.criarProduto(
            nome2, Descricao.of("Desc 2"), Preco.of("200.00"), 
            Categoria.OUTROS, QuantidadeEstoque.of(20));

        // When/Then - Tentar atualizar produto2 para ter o mesmo nome do produto1
        assertThrows(ProdutoJaExisteException.class, () -> {
            produtoService.atualizarProduto(
                produto2Id, nome1, Descricao.of("Nova desc"), 
                Preco.of("300.00"), Categoria.OUTROS, QuantidadeEstoque.of(30));
        });
    }

    @Test
    @Order(5)
    @DisplayName("Deve excluir produto com sucesso")
    void deveExcluirProdutoComSucesso() throws Exception {
        // Given
        Nome nome = Nome.of("Produto Para Excluir");
        ProdutoId produtoId = produtoService.criarProduto(
            nome, Descricao.of("Descrição"), Preco.of("100.00"), 
            Categoria.OUTROS, QuantidadeEstoque.of(10));

        // Verificar que produto existe
        assertDoesNotThrow(() -> produtoService.obterProduto(produtoId));

        // When
        produtoService.excluirProduto(produtoId);

        // Then
        assertThrows(ProdutoNaoEncontradoException.class, () -> {
            produtoService.obterProduto(produtoId);
        });
    }

    @Test
    @Order(6)
    @DisplayName("Deve listar todos os produtos")
    void deveListarTodosProdutos() throws Exception {
        // Given
        produtoService.criarProduto(
            Nome.of("Produto A"), Descricao.of("Desc A"), Preco.of("100.00"), 
            Categoria.ELETRONICOS, QuantidadeEstoque.of(10));
            
        produtoService.criarProduto(
            Nome.of("Produto B"), Descricao.of("Desc B"), Preco.of("200.00"), 
            Categoria.LIVROS, QuantidadeEstoque.of(20));
            
        produtoService.criarProduto(
            Nome.of("Produto C"), Descricao.of("Desc C"), Preco.of("300.00"), 
            Categoria.ROUPAS, QuantidadeEstoque.of(30));

        // When
        List<Produto> produtos = produtoService.obterTodosProdutos();

        // Then
        assertEquals(3, produtos.size());
        
        // Verificar que produtos estão ordenados por nome
        assertEquals("Produto A", produtos.get(0).getNome().getValue());
        assertEquals("Produto B", produtos.get(1).getNome().getValue());
        assertEquals("Produto C", produtos.get(2).getNome().getValue());
    }

    @Test
    @Order(7)
    @DisplayName("Deve buscar produto por nome")
    void deveBuscarProdutoPorNome() throws Exception {
        // Given
        Nome nome = Nome.of("Produto Específico");
        ProdutoId produtoId = produtoService.criarProduto(
            nome, Descricao.of("Descrição específica"), Preco.of("199.99"), 
            Categoria.ESPORTES, QuantidadeEstoque.of(15));

        // When
        Produto produtoEncontrado = produtoService.obterProduto(nome);

        // Then
        assertEquals(produtoId, produtoEncontrado.getId());
        assertEquals(nome, produtoEncontrado.getNome());
    }

    @Test
    @Order(8)
    @DisplayName("Deve reduzir estoque com sucesso")
    void deveReduzirEstoqueComSucesso() throws Exception {
        // Given
        ProdutoId produtoId = produtoService.criarProduto(
            Nome.of("Produto Estoque"), Descricao.of("Desc"), Preco.of("100.00"), 
            Categoria.OUTROS, QuantidadeEstoque.of(50));

        // When
        produtoService.reduzirEstoque(produtoId, QuantidadeEstoque.of(20));

        // Then
        Produto produtoAtualizado = produtoService.obterProduto(produtoId);
        assertEquals(QuantidadeEstoque.of(30), produtoAtualizado.getQuantidadeEstoque());
    }

    @Test
    @Order(9)
    @DisplayName("Deve rejeitar redução de estoque insuficiente")
    void deveRejeitarReducaoDeEstoqueInsuficiente() throws Exception {
        // Given
        ProdutoId produtoId = produtoService.criarProduto(
            Nome.of("Produto Pouco Estoque"), Descricao.of("Desc"), Preco.of("100.00"), 
            Categoria.OUTROS, QuantidadeEstoque.of(10));

        // When/Then
        assertThrows(EstoqueInsuficienteException.class, () -> {
            produtoService.reduzirEstoque(produtoId, QuantidadeEstoque.of(20));
        });
        
        // Verificar que estoque não foi alterado
        Produto produto = produtoService.obterProduto(produtoId);
        assertEquals(QuantidadeEstoque.of(10), produto.getQuantidadeEstoque());
    }

    @Test
    @Order(10)
    @DisplayName("Deve aumentar estoque com sucesso")
    void deveAumentarEstoqueComSucesso() throws Exception {
        // Given
        ProdutoId produtoId = produtoService.criarProduto(
            Nome.of("Produto Reposição"), Descricao.of("Desc"), Preco.of("100.00"), 
            Categoria.OUTROS, QuantidadeEstoque.of(10));

        // When
        produtoService.aumentarEstoque(produtoId, QuantidadeEstoque.of(25));

        // Then
        Produto produtoAtualizado = produtoService.obterProduto(produtoId);
        assertEquals(QuantidadeEstoque.of(35), produtoAtualizado.getQuantidadeEstoque());
    }

    @Test
    @Order(11)
    @DisplayName("Deve listar produtos por categoria")
    void deveListarProdutosPorCategoria() throws Exception {
        // Given
        produtoService.criarProduto(
            Nome.of("Livro A"), Descricao.of("Desc A"), Preco.of("50.00"), 
            Categoria.LIVROS, QuantidadeEstoque.of(10));
            
        produtoService.criarProduto(
            Nome.of("Smartphone"), Descricao.of("Desc B"), Preco.of("1500.00"), 
            Categoria.ELETRONICOS, QuantidadeEstoque.of(5));
            
        produtoService.criarProduto(
            Nome.of("Livro B"), Descricao.of("Desc C"), Preco.of("75.00"), 
            Categoria.LIVROS, QuantidadeEstoque.of(8));

        // When
        List<Produto> livros = produtoService.obterProdutosPorCategoria(Categoria.LIVROS);

        // Then
        assertEquals(2, livros.size());
        assertTrue(livros.stream().allMatch(p -> p.getCategoria() == Categoria.LIVROS));
        
        // Verificar ordenação por nome
        assertEquals("Livro A", livros.get(0).getNome().getValue());
        assertEquals("Livro B", livros.get(1).getNome().getValue());
    }

    @Test
    @Order(12)
    @DisplayName("Deve listar produtos sem estoque")
    void deveListarProdutosSemEstoque() throws Exception {
        // Given
        produtoService.criarProduto(
            Nome.of("Produto Com Estoque"), Descricao.of("Desc"), Preco.of("100.00"), 
            Categoria.OUTROS, QuantidadeEstoque.of(10));
            
        ProdutoId produtoSemEstoque = produtoService.criarProduto(
            Nome.of("Produto Sem Estoque"), Descricao.of("Desc"), Preco.of("200.00"), 
            Categoria.OUTROS, QuantidadeEstoque.of(5));
            
        // Reduzir estoque para zero
        produtoService.reduzirEstoque(produtoSemEstoque, QuantidadeEstoque.of(5));

        // When
        List<Produto> produtosSemEstoque = produtoService.obterProdutosSemEstoque();

        // Then
        assertEquals(1, produtosSemEstoque.size());
        assertEquals("Produto Sem Estoque", produtosSemEstoque.get(0).getNome().getValue());
        assertTrue(produtosSemEstoque.get(0).getQuantidadeEstoque().ehZero());
    }

    @Test
    @Order(13)
    @DisplayName("Deve listar produtos com estoque baixo")
    void deveListarProdutosComEstoqueBaixo() throws Exception {
        // Given
        produtoService.criarProduto(
            Nome.of("Produto Estoque Alto"), Descricao.of("Desc"), Preco.of("100.00"), 
            Categoria.OUTROS, QuantidadeEstoque.of(20));
            
        produtoService.criarProduto(
            Nome.of("Produto Estoque Baixo A"), Descricao.of("Desc"), Preco.of("200.00"), 
            Categoria.OUTROS, QuantidadeEstoque.of(3));
            
        produtoService.criarProduto(
            Nome.of("Produto Estoque Baixo B"), Descricao.of("Desc"), Preco.of("300.00"), 
            Categoria.OUTROS, QuantidadeEstoque.of(1));

        // When
        List<Produto> produtosEstoqueBaixo = produtoService.obterProdutosComEstoqueBaixo(5);

        // Then
        assertEquals(2, produtosEstoqueBaixo.size());
        assertTrue(produtosEstoqueBaixo.stream()
            .allMatch(p -> p.getQuantidadeEstoque().getValue() < 5));
            
        // Verificar ordenação por quantidade de estoque
        assertEquals(1, produtosEstoqueBaixo.get(0).getQuantidadeEstoque().getValue());
        assertEquals(3, produtosEstoqueBaixo.get(1).getQuantidadeEstoque().getValue());
    }

    @Test
    @Order(14)
    @DisplayName("Deve contar total de produtos")
    void deveContarTotalDeProdutos() throws Exception {
        // Given
        produtoService.criarProduto(
            Nome.of("Produto 1"), Descricao.of("Desc 1"), Preco.of("100.00"), 
            Categoria.OUTROS, QuantidadeEstoque.of(10));
            
        produtoService.criarProduto(
            Nome.of("Produto 2"), Descricao.of("Desc 2"), Preco.of("200.00"), 
            Categoria.OUTROS, QuantidadeEstoque.of(20));
            
        produtoService.criarProduto(
            Nome.of("Produto 3"), Descricao.of("Desc 3"), Preco.of("300.00"), 
            Categoria.OUTROS, QuantidadeEstoque.of(30));

        // When
        long totalProdutos = produtoService.obterTotalDeProdutos();

        // Then
        assertEquals(3, totalProdutos);
    }

    @Test
    @Order(15)
    @DisplayName("Deve lançar exceção ao buscar produto inexistente")
    void deveLancarExcecaoAoBuscarProdutoInexistente() {
        // Given
        ProdutoId idInexistente = ProdutoId.gerar();

        // When/Then
        assertThrows(ProdutoNaoEncontradoException.class, () -> {
            produtoService.obterProduto(idInexistente);
        });
    }

    @Test
    @Order(16)
    @DisplayName("Deve lançar exceção ao atualizar produto inexistente")
    void deveLancarExcecaoAoAtualizarProdutoInexistente() {
        // Given
        ProdutoId idInexistente = ProdutoId.gerar();

        // When/Then
        assertThrows(ProdutoNaoEncontradoException.class, () -> {
            produtoService.atualizarProduto(
                idInexistente, Nome.of("Nome"), Descricao.of("Desc"), 
                Preco.of("100.00"), Categoria.OUTROS, QuantidadeEstoque.of(10));
        });
    }

    @Test
    @Order(17)
    @DisplayName("Deve lançar exceção ao excluir produto inexistente")
    void deveLancarExcecaoAoExcluirProdutoInexistente() {
        // Given
        ProdutoId idInexistente = ProdutoId.gerar();

        // When/Then
        assertThrows(ProdutoNaoEncontradoException.class, () -> {
            produtoService.excluirProduto(idInexistente);
        });
    }
}