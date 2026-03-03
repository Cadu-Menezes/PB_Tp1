package com.faculdade.produtos.selenium.paginas;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/*
    Page Object para o modal de atualização de estoque.
    Encapsula a interação com o campo de quantidade e botões.
*/
public class ModalEstoque {

    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(id = "modal-estoque")
    private WebElement modal;

    @FindBy(id = "nome-produto-estoque")
    private WebElement nomeProduto;

    @FindBy(id = "estoque-atual")
    private WebElement estoqueAtual;

    @FindBy(id = "nova-quantidade")
    private WebElement campoNovaQuantidade;

    @FindBy(id = "btn-salvar-estoque")
    private WebElement btnSalvar;

    @FindBy(css = "#modal-estoque .btn-secondary")
    private WebElement btnCancelar;

    public ModalEstoque(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
        aguardarModalAbrir();
    }

    // === Ações ===

    public ModalEstoque preencherNovaQuantidade(String quantidade) {
        wait.until(ExpectedConditions.visibilityOf(campoNovaQuantidade));
        campoNovaQuantidade.clear();
        campoNovaQuantidade.sendKeys(quantidade);
        return this;
    }

    public PaginaPrincipal salvar() {
        wait.until(ExpectedConditions.elementToBeClickable(btnSalvar));
        btnSalvar.click();
        aguardarModalFechar();
        return new PaginaPrincipal(driver);
    }

    public PaginaPrincipal cancelar() {
        wait.until(ExpectedConditions.elementToBeClickable(btnCancelar));
        btnCancelar.click();
        aguardarModalFechar();
        return new PaginaPrincipal(driver);
    }

    // === Consultas ===

    public String obterNomeProduto() { return nomeProduto.getText().trim(); }
    public String obterEstoqueAtual() { return estoqueAtual.getText().trim(); }

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
