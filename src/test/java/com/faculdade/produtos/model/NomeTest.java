package com.faculdade.produtos.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.junit.jupiter.api.Assertions.*;

/*
    Testes para a classe Nome.
    Cobre validações, limites e comportamento esperado.
*/
@DisplayName("Testes do Value Object Nome")
class NomeTest {

    @Test
    @DisplayName("Deve criar nome válido com sucesso")
    void deveCriarNomeValidoComSucesso() {
        // Given
        String nomeValido = "Produto Teste";
        
        // When
        Nome nome = Nome.of(nomeValido);
        
        // Then
        assertNotNull(nome);
        assertEquals(nomeValido, nome.getValue());
    }

    @Test
    @DisplayName("Deve normalizar nome removendo espaços extras")
    void deveNormalizarNomeRemovendoEspacosExtras() {
        // Given
        String nomeComEspacos = "  Produto Teste  ";
        String nomeEsperado = "Produto Teste";
        
        // When
        Nome nome = Nome.of(nomeComEspacos);
        
        // Then
        assertEquals(nomeEsperado, nome.getValue());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Deve rejeitar nomes nulos ou vazios")
    void deveRejeitarNomesNulosOuVazios(String nomeInvalido) {
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> {
            Nome.of(nomeInvalido);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"   ", "\t\t", "\n\n", "   \t\n   "})
    @DisplayName("Deve rejeitar nomes com apenas espaços em branco")
    void deveRejeitarNomesComApenasEspacosEmBranco(String nomeComEspacos) {
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> {
            Nome.of(nomeComEspacos);
        });
    }

    @Test
    @DisplayName("Deve aceitar nome com 1 caractere (limite mínimo)")
    void deveAceitarNomeComUmCaractere() {
        // Given
        String nomeMinimo = "A";
        
        // When
        Nome nome = Nome.of(nomeMinimo);
        
        // Then
        assertNotNull(nome);
        assertEquals(nomeMinimo, nome.getValue());
    }

    @Test
    @DisplayName("Deve aceitar nome com 100 caracteres (limite máximo)")
    void deveAceitarNomeComCemCaracteres() {
        // Given
        String nomeMaximo = "A".repeat(100);
        
        // When
        Nome nome = Nome.of(nomeMaximo);
        
        // Then
        assertNotNull(nome);
        assertEquals(nomeMaximo, nome.getValue());
    }

    @Test
    @DisplayName("Deve rejeitar nome com mais de 100 caracteres")
    void deveRejeitarNomeComMaisDeCemCaracteres() {
        // Given
        String nomeMuitoLongo = "A".repeat(101);
        
        // When/Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            Nome.of(nomeMuitoLongo);
        });
        
        // Verifica mensagem específica
        assertTrue(exception.getMessage().contains("não pode exceder 100 caracteres"));
    }

    @Test
    @DisplayName("Deve implementar equals corretamente")
    void deveImplementarEqualsCorretamente() {
        // Given
        Nome nome1 = Nome.of("Produto Teste");
        Nome nome2 = Nome.of("Produto Teste");
        Nome nome3 = Nome.of("Outro Produto");
        
        // When/Then
        assertEquals(nome1, nome2);
        assertNotEquals(nome1, nome3);
        assertEquals(nome1, nome1); // reflexivo
    }

    @Test
    @DisplayName("Deve implementar hashCode corretamente")
    void deveImplementarHashCodeCorretamente() {
        // Given
        Nome nome1 = Nome.of("Produto Teste");
        Nome nome2 = Nome.of("Produto Teste");
        
        // When/Then
        assertEquals(nome1.hashCode(), nome2.hashCode());
    }

    @Test
    @DisplayName("Deve implementar toString corretamente")
    void deveImplementarToStringCorretamente() {
        // Given
        String valorNome = "Produto Teste";
        Nome nome = Nome.of(valorNome);
        
        // When/Then
        assertEquals(valorNome, nome.toString());
    }

    @Test
    @DisplayName("Deve tratar caracteres especiais corretamente")
    void deveTratarCaracteresEspeciaisCorretamente() {
        // Given
        String nomeComCaracteresEspeciais = "Açúcar & Café™";
        
        // When
        Nome nome = Nome.of(nomeComCaracteresEspeciais);
        
        // Then
        assertNotNull(nome);
        assertEquals(nomeComCaracteresEspeciais, nome.getValue());
    }

    @Test
    @DisplayName("Deve aceitar nomes com números")
    void deveAceitarNomesComNumeros() {
        // Given
        String nomeComNumeros = "Produto 123";
        
        // When
        Nome nome = Nome.of(nomeComNumeros);
        
        // Then
        assertNotNull(nome);
        assertEquals(nomeComNumeros, nome.getValue());
    }

    @Test
    @DisplayName("Deve validar mensagem de erro para nome vazio após trim")
    void deveValidarMensagemDeErroParaNomeVazioAposTrim() {
        // Given
        String nomeVazio = "   ";
        
        // When/Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            Nome.of(nomeVazio);
        });
        
        assertTrue(exception.getMessage().contains("não pode estar vazio"));
    }

    @Test
    @DisplayName("Deve validar mensagem de erro para nome nulo")
    void deveValidarMensagemDeErroParaNomeNulo() {
        // When/Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            Nome.of(null);
        });
        
        assertTrue(exception.getMessage().contains("não pode ser nulo"));
    }

    @Test
    @DisplayName("Deve validar comportamento com string contendo quebras de linha")
    void deveValidarComportamentoComStringContendoQuebrasLinha() {
        // Given
        String nomeComQuebraLinha = "Produto\nTeste";
        
        // When/Then - Should be accepted as it's not empty after trim
        Nome nome = Nome.of(nomeComQuebraLinha);
        assertNotNull(nome);
        assertEquals(nomeComQuebraLinha, nome.getValue());
    }
}