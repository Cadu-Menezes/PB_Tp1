package com.faculdade.produtos.selenium.paginas;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/*
    Page Object para o modal de criação e edição de produto.
    Encapsula a interação com os campos do formulário e botões de ação.
*/
public class ModalProduto {

    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(id = "modal-produto")
    private WebElement modal;

    @FindBy(id = "modal-titulo")
    private WebElement titulo;

    @FindBy(id = "produto-id")
    private WebElement campoId;

    @FindBy(id = "produto-nome")
    private WebElement campoNome;

    @FindBy(id = "produto-descricao")
    private WebElement campoDescricao;

    @FindBy(id = "produto-preco")
    private WebElement campoPreco;

    @FindBy(id = "produto-categoria")
    private WebElement campoCategoria;

    @FindBy(id = "produto-estoque")
    private WebElement campoEstoque;

    @FindBy(id = "btn-salvar-produto")
    private WebElement btnSalvar;

    @FindBy(css = "#modal-produto .btn-secondary")
    private WebElement btnCancelar;

    public ModalProduto(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
        aguardarModalAbrir();
    }

    // === Preenchimento ===

    public ModalProduto preencherNome(String nome) {
        wait.until(ExpectedConditions.visibilityOf(campoNome));
        campoNome.clear();
        campoNome.sendKeys(nome);
        return this;
    }

    public ModalProduto preencherDescricao(String descricao) {
        campoDescricao.clear();
        campoDescricao.sendKeys(descricao);
        return this;
    }

    public ModalProduto preencherPreco(String preco) {
        campoPreco.clear();
        campoPreco.sendKeys(preco);
        return this;
    }

    public ModalProduto selecionarCategoria(String categoria) {
        Select select = new Select(campoCategoria);
        select.selectByValue(categoria);
        return this;
    }

    public ModalProduto preencherEstoque(String estoque) {
        campoEstoque.clear();
        campoEstoque.sendKeys(estoque);
        return this;
    }

    public ModalProduto preencherFormulario(String nome, String descricao,
                                             String preco, String categoria, String estoque) {
        preencherNome(nome);
        preencherDescricao(descricao);
        preencherPreco(preco);
        selecionarCategoria(categoria);
        preencherEstoque(estoque);
        return this;
    }

    // === Ações ===

    public PaginaPrincipal salvar() {
        wait.until(ExpectedConditions.elementToBeClickable(btnSalvar));
        btnSalvar.click();
        aguardarModalFechar();
        return new PaginaPrincipal(driver);
    }

    public ModalProduto salvarComErro() {
        wait.until(ExpectedConditions.elementToBeClickable(btnSalvar));
        btnSalvar.click();
        try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        return this;
    }

    public PaginaPrincipal cancelar() {
        wait.until(ExpectedConditions.elementToBeClickable(btnCancelar));
        btnCancelar.click();
        aguardarModalFechar();
        return new PaginaPrincipal(driver);
    }

    // === Consultas ===

    public String obterTitulo() { return titulo.getText().trim(); }
    public String obterNome() { return campoNome.getAttribute("value"); }
    public String obterDescricao() { return campoDescricao.getAttribute("value"); }
    public String obterPreco() { return campoPreco.getAttribute("value"); }
    public String obterEstoque() { return campoEstoque.getAttribute("value"); }

    public String obterCategoria() {
        Select select = new Select(campoCategoria);
        return select.getFirstSelectedOption().getAttribute("value");
    }

    public boolean isVisivel() {
        try {
            return modal.getAttribute("class").contains("show");
        } catch (Exception e) {
            return false;
        }
    }

    // === Utilitários ===

    private void aguardarModalAbrir() {
        wait.until(ExpectedConditions.attributeContains(modal, "class", "show"));
        try { Thread.sleep(400); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    private void aguardarModalFechar() {
        wait.until(ExpectedConditions.not(
                ExpectedConditions.attributeContains(modal, "class", "show")));
        try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
