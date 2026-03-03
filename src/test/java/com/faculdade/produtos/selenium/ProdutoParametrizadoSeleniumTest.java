package com.faculdade.produtos.selenium;

import com.faculdade.produtos.selenium.paginas.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/*
    Testes parametrizados Selenium para validar diferentes cenários
    e entradas de dados no CRUD de produtos.
    Utiliza @ParameterizedTest com @CsvSource, @MethodSource e @ValueSource
    para cobrir múltiplas combinações de dados.
*/
@DisplayName("Testes Selenium Parametrizados - Cenários de Dados")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProdutoParametrizadoSeleniumTest extends BaseSeleniumTest {

    // =============================================
    // TESTE PARAMETRIZADO: Criação com diferentes categorias
    // =============================================

    @ParameterizedTest(name = "Deve criar produto na categoria {0}")
    @Order(1)
    @DisplayName("Deve criar produto em cada categoria disponível")
    @CsvSource({
            "ELETRONICOS,  Smartphone X,    Celular top,       2999.99, 10",
            "ROUPAS,       Camisa Polo,     Algodao premium,    89.90,  50",
            "LIVROS,       Clean Code,      Robert C Martin,   120.00,  20",
            "CASA_E_JARDIM, Vaso Decorativo, Ceramica artesanal, 45.00, 30",
            "ESPORTES,     Bola de Futebol, Campo oficial,      79.90,  15",
            "BELEZA,       Perfume Floral,  Eau de parfum,     250.00,   8",
            "ALIMENTACAO,  Cafe Especial,   Graos torrados,     35.00, 100",
            "BRINQUEDOS,   Lego Classic,    500 pecas,         299.90,  12",
            "FERRAMENTAS,  Furadeira Pro,   Impacto 750W,      349.00,   6",
            "OUTROS,       Pen Drive,       64GB USB 3.0,       39.90,  40"
    })
    void deveCriarProdutoEmCadaCategoria(String categoria, String nome,
                                          String descricao, String preco, String estoque) {
        criarProdutoViaInterface(nome, descricao, preco, categoria, estoque);
        aguardarAtualizacao();

        assertTrue(paginaPrincipal.existeAlertaSucesso(),
                "Deve exibir alerta de sucesso ao criar produto na categoria " + categoria);
        assertEquals(1, paginaPrincipal.obterQuantidadeProdutos(),
                "Deve haver 1 produto na tabela");
        assertEquals(nome, paginaPrincipal.obterNomeProduto(0),
                "O nome do produto deve corresponder ao criado");
    }

    // =============================================
    // TESTE PARAMETRIZADO: Diferentes valores de preço
    // =============================================

    @ParameterizedTest(name = "Deve aceitar preco R$ {0}")
    @Order(2)
    @DisplayName("Deve criar produto com diferentes valores de preço válidos")
    @CsvSource({
            "0.01,   Produto Centavo,     Preco minimo",
            "9.99,   Produto Barato,      Preco baixo",
            "99.99,  Produto Medio,       Preco medio",
            "999.99, Produto Caro,        Preco alto",
            "9999.99,Produto Premium,     Preco muito alto"
    })
    void deveCriarProdutoComDiferentesPrecos(String preco, String nome, String descricao) {
        criarProdutoViaInterface(nome, descricao, preco, "ELETRONICOS", "10");
        aguardarAtualizacao();

        assertTrue(paginaPrincipal.existeAlertaSucesso(),
                "Deve aceitar o preco R$ " + preco);
        assertEquals(1, paginaPrincipal.obterQuantidadeProdutos());
    }

    // =============================================
    // TESTE PARAMETRIZADO: Diferentes valores de estoque
    // =============================================

    @ParameterizedTest(name = "Deve aceitar estoque: {0}")
    @Order(3)
    @DisplayName("Deve criar produto com diferentes quantidades de estoque")
    @ValueSource(strings = {"0", "1", "5", "10", "50", "100"})
    void deveCriarProdutoComDiferentesEstoques(String estoque) {
        String nome = "Produto Estoque " + estoque;
        criarProdutoViaInterface(nome, "Teste de estoque", "29.90", "OUTROS", estoque);
        aguardarAtualizacao();

        assertTrue(paginaPrincipal.existeAlertaSucesso(),
                "Deve aceitar estoque: " + estoque);
        assertEquals(estoque, paginaPrincipal.obterEstoqueProduto(0),
                "O estoque na tabela deve ser " + estoque);
    }

    // =============================================
    // TESTE PARAMETRIZADO: Busca por diferentes termos
    // =============================================

    @ParameterizedTest(name = "Buscar por ''{0}'' deve encontrar {1} produto(s)")
    @Order(4)
    @DisplayName("Deve filtrar corretamente ao buscar por diferentes termos")
    @CsvSource({
            "Note,     1",
            "Mouse,    1",
            "e,        3",
            "XYZ,      0"
    })
    void deveFiltrarPorDiferentesTermos(String termoBusca, int resultadosEsperados) {
        criarProdutoViaInterface("Notebook HP", "Notebook i5", "3000.00", "ELETRONICOS", "5");
        aguardarAtualizacao();
        criarProdutoViaInterface("Mouse Sem Fio", "Wireless", "80.00", "ELETRONICOS", "20");
        aguardarAtualizacao();
        criarProdutoViaInterface("Teclado Gamer", "Mecanico", "250.00", "ELETRONICOS", "10");
        aguardarAtualizacao();

        paginaPrincipal.buscarProduto(termoBusca);

        assertEquals(resultadosEsperados, paginaPrincipal.obterQuantidadeProdutos(),
                "A busca por '" + termoBusca + "' deve retornar " + resultadosEsperados + " resultado(s)");
    }

    // =============================================
    // TESTE PARAMETRIZADO: Atualização de estoque
    // =============================================

    @ParameterizedTest(name = "Atualizar estoque de {0} para {1}")
    @Order(5)
    @DisplayName("Deve atualizar estoque para diferentes valores")
    @CsvSource({
            "10, 0",
            "10, 50",
            "0,  25"
    })
    void deveAtualizarEstoqueParaDiferentesValores(String estoqueInicial, String novoEstoque) {
        String nome = "Produto Est " + estoqueInicial + " para " + novoEstoque;
        criarProdutoViaInterface(nome, "Teste", "50.00", "OUTROS", estoqueInicial);
        aguardarAtualizacao();

        ModalEstoque modal = paginaPrincipal.clicarEstoqueProduto(0);
        modal.preencherNovaQuantidade(novoEstoque);
        paginaPrincipal = modal.salvar();
        aguardarAtualizacao();

        assertTrue(paginaPrincipal.existeAlertaSucesso(),
                "Deve atualizar estoque de " + estoqueInicial + " para " + novoEstoque);
        assertEquals(novoEstoque, paginaPrincipal.obterEstoqueProduto(0),
                "O estoque na tabela deve ser " + novoEstoque);
    }

    // =============================================
    // TESTE PARAMETRIZADO: Nomes com diferentes tamanhos
    // =============================================

    @ParameterizedTest(name = "Nome com {0} caractere(s)")
    @Order(6)
    @DisplayName("Deve aceitar nomes com tamanhos variados dentro do limite")
    @MethodSource("nomesValidos")
    void deveCriarProdutoComNomesDeTamanhosVariados(String nome) {
        criarProdutoViaInterface(nome, "Teste de tamanho de nome", "10.00", "OUTROS", "1");
        aguardarAtualizacao();

        assertTrue(paginaPrincipal.existeAlertaSucesso(),
                "Deve aceitar nome com " + nome.length() + " caractere(s)");
        assertEquals(1, paginaPrincipal.obterQuantidadeProdutos());
    }

    static Stream<String> nomesValidos() {
        return Stream.of(
                "A",
                "AB",
                "Produto Normal",
                "Nome Com Cinquenta Caract Exatos Para Testar Limites!",
                "A".repeat(100)
        );
    }

    // =============================================
    // TESTE PARAMETRIZADO: Edição de campo
    // =============================================

    @ParameterizedTest(name = "Editar nome para: {0}")
    @Order(7)
    @DisplayName("Deve editar o nome do produto com diferentes valores")
    @CsvSource({
            "Novo Nome A",
            "Novo Nome B",
            "Novo Nome C"
    })
    void deveEditarNomeDoProduto(String novoNome) {
        criarProdutoViaInterface("Produto Base", "Desc base", "50.00", "LIVROS", "10");
        aguardarAtualizacao();

        ModalProduto modal = paginaPrincipal.clicarEditarProduto(0);
        modal.preencherNome(novoNome);
        paginaPrincipal = modal.salvar();
        aguardarAtualizacao();

        assertTrue(paginaPrincipal.existeAlertaSucesso(),
                "Deve salvar com sucesso ao editar nome para: " + novoNome);
        assertEquals(novoNome, paginaPrincipal.obterNomeProduto(0),
                "O nome na tabela deve ser: " + novoNome);
    }

    // =============================================
    // TESTE PARAMETRIZADO: Classificação de estoque
    // =============================================

    @ParameterizedTest(name = "Estoque {0} deve ser: {1}")
    @Order(8)
    @DisplayName("Deve categorizar corretamente os produtos por nível de estoque")
    @CsvSource({
            "0,   semEstoque",
            "1,   estoqueBaixo",
            "3,   estoqueBaixo",
            "4,   estoqueBaixo",
            "5,   normal",
            "50,  normal"
    })
    void deveClassificarEstoqueCorretamente(String estoque, String classificacao) {
        String nome = "Produto Classif " + estoque;
        criarProdutoViaInterface(nome, "Classificacao", "10.00", "OUTROS", estoque);
        aguardarAtualizacao();

        int semEstoque = paginaPrincipal.obterStatSemEstoque();
        int estoqueBaixo = paginaPrincipal.obterStatEstoqueBaixo();

        switch (classificacao) {
            case "semEstoque" -> {
                assertEquals(1, semEstoque, "Estoque " + estoque + " deve ser sem estoque");
                assertEquals(0, estoqueBaixo, "Estoque " + estoque + " nao deve ser estoque baixo");
            }
            case "estoqueBaixo" -> {
                assertEquals(0, semEstoque, "Estoque " + estoque + " nao deve ser sem estoque");
                assertEquals(1, estoqueBaixo, "Estoque " + estoque + " deve ser estoque baixo");
            }
            case "normal" -> {
                assertEquals(0, semEstoque, "Estoque " + estoque + " nao deve ser sem estoque");
                assertEquals(0, estoqueBaixo, "Estoque " + estoque + " nao deve ser estoque baixo");
            }
        }
    }
}
