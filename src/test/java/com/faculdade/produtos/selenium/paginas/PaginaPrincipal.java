package com.faculdade.produtos.selenium.paginas;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/*
    Page Object para a página principal de listagem de produtos.
    Encapsula a interação com a tabela, estatísticas, busca, filtros e botões.
*/
public class PaginaPrincipal {

    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(id = "navbar")
    private WebElement navbar;

    @FindBy(id = "stat-total")
    private WebElement statTotal;

    @FindBy(id = "stat-sem-estoque")
    private WebElement statSemEstoque;

    @FindBy(id = "stat-estoque-baixo")
    private WebElement statEstoqueBaixo;

    @FindBy(id = "btn-novo-produto")
    private WebElement btnNovoProduto;

    @FindBy(id = "campo-busca")
    private WebElement campoBusca;

    @FindBy(id = "filtro-categoria")
    private WebElement filtroCategoria;

    @FindBy(id = "tabela-produtos")
    private WebElement tabelaProdutos;

    @FindBy(id = "corpo-tabela-produtos")
    private WebElement corpoTabela;

    @FindBy(id = "msg-lista-vazia")
    private WebElement msgListaVazia;

    @FindBy(id = "alertas-container")
    private WebElement alertasContainer;

    public PaginaPrincipal(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    /*
        Navega para a página principal e aguarda carregamento completo.
    */
    public PaginaPrincipal navegar(String urlBase) {
        driver.get(urlBase);
        wait.until(ExpectedConditions.visibilityOf(navbar));
        aguardarBootstrapCarregar();
        aguardarDadosCarregarem();
        return this;
    }

    // === Ações ===

    public ModalProduto clicarNovoProduto() {
        wait.until(ExpectedConditions.elementToBeClickable(btnNovoProduto));
        btnNovoProduto.click();
        return new ModalProduto(driver);
    }

    public PaginaPrincipal buscarProduto(String texto) {
        campoBusca.clear();
        campoBusca.sendKeys(texto);
        aguardarFiltro();
        return this;
    }

    public PaginaPrincipal filtrarPorCategoria(String categoria) {
        Select select = new Select(filtroCategoria);
        select.selectByValue(categoria);
        aguardarFiltro();
        return this;
    }

    public ModalProduto clicarEditarProduto(int indice) {
        List<WebElement> linhas = corpoTabela.findElements(By.tagName("tr"));
        WebElement btnEditar = linhas.get(indice).findElement(By.cssSelector("[onclick*='abrirModalEditar']"));
        wait.until(ExpectedConditions.elementToBeClickable(btnEditar));
        btnEditar.click();
        return new ModalProduto(driver);
    }

    public ModalExcluir clicarExcluirProduto(int indice) {
        List<WebElement> linhas = corpoTabela.findElements(By.tagName("tr"));
        WebElement btnExcluir = linhas.get(indice).findElement(By.cssSelector("[onclick*='abrirModalExcluir']"));
        wait.until(ExpectedConditions.elementToBeClickable(btnExcluir));
        btnExcluir.click();
        return new ModalExcluir(driver);
    }

    public ModalEstoque clicarEstoqueProduto(int indice) {
        List<WebElement> linhas = corpoTabela.findElements(By.tagName("tr"));
        WebElement btnEstoque = linhas.get(indice).findElement(By.cssSelector("[onclick*='abrirModalEstoque']"));
        wait.until(ExpectedConditions.elementToBeClickable(btnEstoque));
        btnEstoque.click();
        return new ModalEstoque(driver);
    }

    // === Consultas ===

    public int obterStatTotal() {
        wait.until(ExpectedConditions.visibilityOf(statTotal));
        return Integer.parseInt(statTotal.getText().trim());
    }

    public int obterStatSemEstoque() {
        wait.until(ExpectedConditions.visibilityOf(statSemEstoque));
        return Integer.parseInt(statSemEstoque.getText().trim());
    }

    public int obterStatEstoqueBaixo() {
        wait.until(ExpectedConditions.visibilityOf(statEstoqueBaixo));
        return Integer.parseInt(statEstoqueBaixo.getText().trim());
    }

    public int obterQuantidadeProdutos() {
        List<WebElement> linhas = corpoTabela.findElements(By.tagName("tr"));
        if (linhas.size() == 1) {
            String texto = linhas.get(0).getText().trim();
            if (texto.isEmpty() || texto.contains("Nenhum")) {
                return 0;
            }
        }
        return linhas.size();
    }

    public String obterNomeProduto(int indice) {
        List<WebElement> linhas = corpoTabela.findElements(By.tagName("tr"));
        List<WebElement> colunas = linhas.get(indice).findElements(By.tagName("td"));
        return colunas.get(0).getText().trim();
    }

    public String obterEstoqueProduto(int indice) {
        List<WebElement> linhas = corpoTabela.findElements(By.tagName("tr"));
        List<WebElement> colunas = linhas.get(indice).findElements(By.tagName("td"));
        return colunas.get(4).getText().trim();
    }

    public boolean isTabelaVisivel() {
        try {
            return tabelaProdutos.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isListaVaziaVisivel() {
        try {
            return !msgListaVazia.getAttribute("class").contains("d-none");
        } catch (Exception e) {
            return false;
        }
    }

    public boolean existeAlertaSucesso() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("#alertas-container .alert-success.show")));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // === Utilitários Privados ===

    private void aguardarBootstrapCarregar() {
        wait.until(d -> {
            JavascriptExecutor js = (JavascriptExecutor) d;
            return (Boolean) js.executeScript(
                    "return typeof bootstrap !== 'undefined' && typeof bootstrap.Modal !== 'undefined'");
        });
    }

    private void aguardarDadosCarregarem() {
        try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    private void aguardarFiltro() {
        try { Thread.sleep(300); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
