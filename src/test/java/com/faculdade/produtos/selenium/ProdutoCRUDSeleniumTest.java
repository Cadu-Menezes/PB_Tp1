package com.faculdade.produtos.selenium;

import com.faculdade.produtos.selenium.paginas.*;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/*
    Testes Selenium para o fluxo completo de CRUD de produtos.
    Valida cadastro, listagem, edição, exclusão e atualização de estoque
    através da interface web, interagindo com formulários, tabelas,
    botões e alertas de confirmação.
*/
@DisplayName("Testes Selenium - CRUD de Produtos")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProdutoCRUDSeleniumTest extends BaseSeleniumTest {

    // =============================================
    // TESTES DE LISTAGEM (READ)
    // =============================================

    @Test
    @Order(1)
    @DisplayName("Deve exibir página principal com lista vazia")
    void deveExibirPaginaPrincipalComListaVazia() {
        assertTrue(paginaPrincipal.isListaVaziaVisivel(),
                "A mensagem de lista vazia deve estar visível quando não há produtos");
        assertEquals(0, paginaPrincipal.obterStatTotal(),
                "O stat total deve ser 0");
    }

    @Test
    @Order(2)
    @DisplayName("Deve abrir modal de novo produto ao clicar no botão")
    void deveAbrirModalDeNovoProduto() {
        ModalProduto modal = paginaPrincipal.clicarNovoProduto();

        assertTrue(modal.isVisivel(), "O modal deve estar visível");
        assertTrue(modal.obterTitulo().contains("Novo Produto"),
                "O título deve conter 'Novo Produto'");
        assertEquals("", modal.obterNome(), "O campo nome deve estar vazio");
        assertEquals("", modal.obterDescricao(), "O campo descrição deve estar vazio");
    }

    // =============================================
    // TESTES DE CADASTRO (CREATE)
    // =============================================

    @Test
    @Order(3)
    @DisplayName("Deve criar produto com sucesso via formulário")
    void deveCriarProdutoComSucesso() {
        paginaPrincipal = criarProdutoViaInterface(
                "Notebook Dell", "Notebook i7 16GB RAM", "4599.99", "ELETRONICOS", "10");
        aguardarAtualizacao();

        assertTrue(paginaPrincipal.existeAlertaSucesso(),
                "Deve exibir alerta de sucesso após criar produto");
        assertTrue(paginaPrincipal.isTabelaVisivel(),
                "A tabela deve estar visível após criar produto");
        assertEquals(1, paginaPrincipal.obterQuantidadeProdutos(),
                "Deve haver 1 produto na tabela");
        assertEquals("Notebook Dell", paginaPrincipal.obterNomeProduto(0),
                "O nome do produto na tabela deve ser 'Notebook Dell'");
    }

    @Test
    @Order(4)
    @DisplayName("Deve atualizar estatísticas após criar produto")
    void deveAtualizarEstatisticasAposCriar() {
        paginaPrincipal = criarProdutoViaInterface(
                "Mouse Gamer", "Mouse RGB 6 botões", "199.90", "ELETRONICOS", "25");
        aguardarAtualizacao();

        assertEquals(1, paginaPrincipal.obterStatTotal(),
                "Stat total deve ser 1 após criação");
    }

    @Test
    @Order(5)
    @DisplayName("Deve criar múltiplos produtos e listar todos")
    void deveCriarMultiplosProdutos() {
        criarProdutoViaInterface("Produto A", "Descrição A", "10.00", "LIVROS", "5");
        aguardarAtualizacao();
        criarProdutoViaInterface("Produto B", "Descrição B", "20.00", "ROUPAS", "15");
        aguardarAtualizacao();
        criarProdutoViaInterface("Produto C", "Descrição C", "30.00", "ESPORTES", "8");
        aguardarAtualizacao();

        assertEquals(3, paginaPrincipal.obterQuantidadeProdutos(),
                "Deve haver 3 produtos na tabela");
        assertEquals(3, paginaPrincipal.obterStatTotal(),
                "O stat total deve ser 3");
    }

    @Test
    @Order(6)
    @DisplayName("Deve cancelar criação de produto e manter lista inalterada")
    void deveCancelarCriacaoDeProduto() {
        ModalProduto modal = paginaPrincipal.clicarNovoProduto();
        modal.preencherNome("Produto Cancelado");
        paginaPrincipal = modal.cancelar();

        assertTrue(paginaPrincipal.isListaVaziaVisivel(),
                "A lista deve continuar vazia após cancelamento");
    }

    // =============================================
    // TESTES DE EDIÇÃO (UPDATE)
    // =============================================

    @Test
    @Order(10)
    @DisplayName("Deve editar produto com sucesso")
    void deveEditarProdutoComSucesso() {
        criarProdutoViaInterface("Produto Original", "Desc original", "50.00", "LIVROS", "10");
        aguardarAtualizacao();

        ModalProduto modal = paginaPrincipal.clicarEditarProduto(0);
        modal.preencherNome("Produto Editado");
        modal.preencherPreco("75.00");
        paginaPrincipal = modal.salvar();
        aguardarAtualizacao();

        assertTrue(paginaPrincipal.existeAlertaSucesso(),
                "Deve exibir alerta de sucesso ao editar");
        assertEquals("Produto Editado", paginaPrincipal.obterNomeProduto(0),
                "O nome deve estar atualizado na tabela");
    }

    @Test
    @Order(11)
    @DisplayName("Deve abrir modal de edição com dados preenchidos")
    void deveAbrirModalDeEdicaoComDados() {
        criarProdutoViaInterface("Notebook HP", "Notebook i5 8GB", "3500.00", "ELETRONICOS", "7");
        aguardarAtualizacao();

        ModalProduto modal = paginaPrincipal.clicarEditarProduto(0);

        assertTrue(modal.obterTitulo().contains("Editar"),
                "O título deve conter 'Editar'");
        assertEquals("Notebook HP", modal.obterNome(),
                "O campo nome deve estar preenchido");
        assertFalse(modal.obterPreco().isEmpty(),
                "O campo preço deve estar preenchido");

        modal.cancelar();
    }

    @Test
    @Order(12)
    @DisplayName("Deve cancelar edição e manter dados originais")
    void deveManterDadosAoCancelarEdicao() {
        criarProdutoViaInterface("Produto Fixo", "Desc fixa", "100.00", "ROUPAS", "20");
        aguardarAtualizacao();

        ModalProduto modal = paginaPrincipal.clicarEditarProduto(0);
        modal.preencherNome("Nome Alterado Temporario");
        paginaPrincipal = modal.cancelar();
        aguardarAtualizacao();

        assertEquals("Produto Fixo", paginaPrincipal.obterNomeProduto(0),
                "O nome do produto deve permanecer inalterado após cancelar edição");
    }

    // =============================================
    // TESTES DE EXCLUSÃO (DELETE)
    // =============================================

    @Test
    @Order(20)
    @DisplayName("Deve exibir modal de confirmação ao excluir")
    void deveExibirModalDeConfirmacaoDeExclusao() {
        criarProdutoViaInterface("Produto Para Excluir", "Desc", "30.00", "OUTROS", "5");
        aguardarAtualizacao();

        ModalExcluir modalExcluir = paginaPrincipal.clicarExcluirProduto(0);

        assertTrue(modalExcluir.isVisivel(),
                "O modal de confirmação deve estar visível");
        assertEquals("Produto Para Excluir", modalExcluir.obterNomeProduto(),
                "O nome no modal deve corresponder ao produto");

        modalExcluir.cancelar();
    }

    @Test
    @Order(21)
    @DisplayName("Deve excluir produto com sucesso após confirmação")
    void deveExcluirProdutoComSucesso() {
        criarProdutoViaInterface("Produto Removido", "Desc", "25.00", "BRINQUEDOS", "3");
        aguardarAtualizacao();

        ModalExcluir modalExcluir = paginaPrincipal.clicarExcluirProduto(0);
        paginaPrincipal = modalExcluir.confirmarExclusao();
        aguardarAtualizacao();

        assertTrue(paginaPrincipal.existeAlertaSucesso(),
                "Deve exibir alerta de sucesso ao excluir");
        assertTrue(paginaPrincipal.isListaVaziaVisivel(),
                "A lista deve estar vazia após exclusão");
        assertEquals(0, paginaPrincipal.obterStatTotal(),
                "O stat total deve ser 0 após exclusão");
    }

    @Test
    @Order(22)
    @DisplayName("Deve cancelar exclusão e manter produto")
    void deveCancelarExclusaoEManterProduto() {
        criarProdutoViaInterface("Produto Mantido", "Desc", "40.00", "FERRAMENTAS", "12");
        aguardarAtualizacao();

        ModalExcluir modalExcluir = paginaPrincipal.clicarExcluirProduto(0);
        paginaPrincipal = modalExcluir.cancelar();
        aguardarAtualizacao();

        assertEquals(1, paginaPrincipal.obterQuantidadeProdutos(),
                "O produto deve permanecer na tabela após cancelar exclusão");
    }

    // =============================================
    // TESTES DE ATUALIZAÇÃO DE ESTOQUE
    // =============================================

    @Test
    @Order(30)
    @DisplayName("Deve atualizar estoque com sucesso")
    void deveAtualizarEstoqueComSucesso() {
        criarProdutoViaInterface("Produto Estoque", "Desc", "60.00", "ALIMENTACAO", "10");
        aguardarAtualizacao();

        ModalEstoque modalEstoque = paginaPrincipal.clicarEstoqueProduto(0);
        modalEstoque.preencherNovaQuantidade("50");
        paginaPrincipal = modalEstoque.salvar();
        aguardarAtualizacao();

        assertTrue(paginaPrincipal.existeAlertaSucesso(),
                "Deve exibir alerta de sucesso ao atualizar estoque");
        assertEquals("50", paginaPrincipal.obterEstoqueProduto(0),
                "O estoque na tabela deve ser 50");
    }

    // =============================================
    // TESTES DE BUSCA E FILTRO
    // =============================================

    @Test
    @Order(40)
    @DisplayName("Deve filtrar produtos por busca")
    void deveFiltrarProdutosPorBusca() {
        criarProdutoViaInterface("Notebook HP", "Notebook i5", "3000.00", "ELETRONICOS", "5");
        aguardarAtualizacao();
        criarProdutoViaInterface("Mouse Sem Fio", "Mouse wireless", "80.00", "ELETRONICOS", "20");
        aguardarAtualizacao();

        paginaPrincipal.buscarProduto("Note");

        assertEquals(1, paginaPrincipal.obterQuantidadeProdutos(),
                "Busca por 'Note' deve retornar 1 resultado");
        assertEquals("Notebook HP", paginaPrincipal.obterNomeProduto(0),
                "O produto encontrado deve ser 'Notebook HP'");
    }

    // =============================================
    // TESTES DE FLUXO COMPLETO (E2E)
    // =============================================

    @Test
    @Order(50)
    @DisplayName("Deve executar fluxo completo: criar, editar, atualizar estoque e excluir")
    void deveExecutarFluxoCompleto() {
        // 1. Criar
        paginaPrincipal = criarProdutoViaInterface(
                "Produto E2E", "Teste ponta a ponta", "100.00", "OUTROS", "10");
        aguardarAtualizacao();
        assertEquals(1, paginaPrincipal.obterQuantidadeProdutos());

        // 2. Editar
        ModalProduto modalEditar = paginaPrincipal.clicarEditarProduto(0);
        modalEditar.preencherNome("Produto E2E Editado");
        paginaPrincipal = modalEditar.salvar();
        aguardarAtualizacao();
        assertEquals("Produto E2E Editado", paginaPrincipal.obterNomeProduto(0));

        // 3. Atualizar estoque
        ModalEstoque modalEstoque = paginaPrincipal.clicarEstoqueProduto(0);
        modalEstoque.preencherNovaQuantidade("99");
        paginaPrincipal = modalEstoque.salvar();
        aguardarAtualizacao();
        assertEquals("99", paginaPrincipal.obterEstoqueProduto(0));

        // 4. Excluir
        ModalExcluir modalExcluir = paginaPrincipal.clicarExcluirProduto(0);
        paginaPrincipal = modalExcluir.confirmarExclusao();
        aguardarAtualizacao();
        assertEquals(0, paginaPrincipal.obterStatTotal());
    }
}
