package com.faculdade.produtos.selenium;

import com.faculdade.produtos.config.DatabaseConfig;
import com.faculdade.produtos.config.DatabaseMigration;
import com.faculdade.produtos.repository.impl.RepositorioPostgreSqlProduto;
import com.faculdade.produtos.service.ProdutoService;
import com.faculdade.produtos.selenium.paginas.PaginaPrincipal;
import com.faculdade.produtos.web.WebServer;

import io.github.bonigarcia.wdm.WebDriverManager;

import org.junit.jupiter.api.*;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.Duration;

/*
    Classe base para todos os testes Selenium.
    Configura o servidor web com banco H2 em memória,
    inicializa o WebDriver Chrome em modo headless
    e fornece métodos utilitários compartilhados.
*/
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseSeleniumTest {

    protected static final int PORTA_TESTE = 8888;
    protected static final String URL_BASE = "http://localhost:" + PORTA_TESTE;

    protected WebDriver driver;
    protected PaginaPrincipal paginaPrincipal;

    private WebServer webServer;
    private DataSource dataSource;

    @BeforeAll
    void configurarInfraestrutura() throws Exception {
        // 1. Configurar banco de teste H2
        dataSource = DatabaseConfig.createTestDataSource();
        DatabaseMigration.migrate(dataSource);

        // 2. Configurar serviço com repositório apontando para H2
        RepositorioPostgreSqlProduto repositorio = new RepositorioPostgreSqlProduto(dataSource);
        ProdutoService produtoService = new ProdutoService(repositorio, repositorio);

        // 3. Iniciar servidor web na porta de teste
        webServer = new WebServer(produtoService, PORTA_TESTE);
        webServer.iniciar();

        // 4. Configurar WebDriver (Chrome visível para acompanhamento)
        WebDriverManager.chromedriver().setup();
        ChromeOptions opcoes = new ChromeOptions();
        
        opcoes.addArguments("--no-sandbox");
        opcoes.addArguments("--disable-dev-shm-usage");
        opcoes.addArguments("--disable-gpu");
        opcoes.addArguments("--window-size=1920,1080");
        opcoes.addArguments("--disable-extensions");
        opcoes.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(opcoes);
    }

    @BeforeEach
    void configurarCadaTeste() throws SQLException {
        // Limpar banco antes de cada teste para isolamento
        DatabaseMigration.dropAllTables(dataSource);
        DatabaseMigration.migrate(dataSource);

        // Navegar para a página principal
        paginaPrincipal = new PaginaPrincipal(driver);
        paginaPrincipal.navegar(URL_BASE);

        // Desabilitar animações CSS para evitar problemas de timing no modo visível
        ((JavascriptExecutor) driver).executeScript(
            "var style = document.createElement('style');" +
            "style.innerHTML = '*, *::before, *::after { " +
            "transition-duration: 0s !important; " +
            "animation-duration: 0s !important; " +
            "transition-delay: 0s !important; }';" +
            "document.head.appendChild(style);"
        );
    }

    @AfterAll
    void destruirInfraestrutura() {
        if (driver != null) {
            driver.quit();
        }
        if (webServer != null) {
            webServer.parar();
        }
    }

    // === Métodos Utilitários ===

    /*
        Cria um produto via interface web.
    */
    protected PaginaPrincipal criarProdutoViaInterface(String nome, String descricao,
                                                        String preco, String categoria, String estoque) {
        return paginaPrincipal
                .clicarNovoProduto()
                .preencherFormulario(nome, descricao, preco, categoria, estoque)
                .salvar();
    }

    /*
        Aguarda a interface ser atualizada.
    */
    protected void aguardarAtualizacao() {
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected DataSource getDataSource() {
        return dataSource;
    }
}
