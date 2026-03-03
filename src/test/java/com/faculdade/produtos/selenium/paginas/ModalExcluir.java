package com.faculdade.produtos.selenium.paginas;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/*
    Page Object para o modal de confirmação de exclusão.
    Encapsula a interação com o alerta de confirmação e botões.
*/
public class ModalExcluir {

    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(id = "modal-excluir")
    private WebElement modal;

    @FindBy(id = "nome-produto-excluir")
    private WebElement nomeProdutoExcluir;

    @FindBy(id = "btn-confirmar-excluir")
    private WebElement btnConfirmar;

    @FindBy(css = "#modal-excluir .btn-secondary")
    private WebElement btnCancelar;

    public ModalExcluir(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
        aguardarModalAbrir();
    }

    // === Ações ===

    public PaginaPrincipal confirmarExclusao() {
        wait.until(ExpectedConditions.elementToBeClickable(btnConfirmar));
        btnConfirmar.click();
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

    public String obterNomeProduto() {
        return nomeProdutoExcluir.getText().trim();
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
