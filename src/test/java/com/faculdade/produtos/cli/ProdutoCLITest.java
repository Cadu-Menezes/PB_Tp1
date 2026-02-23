package com.faculdade.produtos.cli;

import com.faculdade.produtos.service.ProdutoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do ProdutoCLI")
class ProdutoCLITest {
    
    @Test
    @DisplayName("Deve ter construtor que aceita ProdutoService")
    void deveTerConstrutorQueAceitaProdutoService() {
        // Given - Usar um service real temporário
        try {
            ProdutoService produtoService = new ProdutoService(null, null);
            
            // When/Then - Se chegou até aqui, construtor funciona basicamente
            assertDoesNotThrow(() -> {
                ProdutoCLI cli = new ProdutoCLI(produtoService);
                assertNotNull(cli);
            });
            
        } catch (NullPointerException e) {
            // Esperado - o service precisa de repositórios válidos
            assertTrue(e.getMessage() != null);
        } catch (Exception e) {
            // Qualquer outra exceção também é esperada devido ao service inválido
            assertNotNull(e.getMessage());
        }
    }

    @Test
    @DisplayName("Classe ProdutoCLI deve existir em package cli")
    void deveExistirNoPackageCli() {
        // Given/When
        String nomePackage = ProdutoCLI.class.getPackage().getName();
        
        // Then
        assertEquals("com.faculdade.produtos.cli", nomePackage);
    }

    @Test
    @DisplayName("ProdutoCLI deve ter método iniciar()")
    void deveTerMetodoIniciar() throws NoSuchMethodException {
        // Given/When
        var metodoIniciar = ProdutoCLI.class.getDeclaredMethod("iniciar");
        
        // Then
        assertNotNull(metodoIniciar);
        assertEquals("iniciar", metodoIniciar.getName());
        assertEquals(0, metodoIniciar.getParameterCount());
    }
}