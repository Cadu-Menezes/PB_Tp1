package com.faculdade.produtos.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para a classe Preco.
 * Cobre validações de preço, operações matemáticas e precisão decimal.
 */
@DisplayName("Testes do Value Object Preco")
class PrecoTest {

    @Test
    @DisplayName("Deve criar preço válido usando BigDecimal")
    void deveCriarPrecoValidoUsandoBigDecimal() {
        // Given
        BigDecimal valorPreco = new BigDecimal("19.99");
        
        // When
        Preco preco = Preco.of(valorPreco);
        
        // Then
        assertNotNull(preco);
        assertEquals(new BigDecimal("19.99"), preco.getValue());
    }

    @Test
    @DisplayName("Deve criar preço válido usando double")
    void deveCriarPrecoValidoUsandoDouble() {
        // Given
        double valorPreco = 29.95;
        
        // When
        Preco preco = Preco.of(valorPreco);
        
        // Then
        assertNotNull(preco);
        assertEquals(new BigDecimal("29.95"), preco.getValue());
    }

    @Test
    @DisplayName("Deve criar preço válido usando string")
    void deveCriarPrecoValidoUsandoString() {
        // Given
        String valorPreco = "39.99";
        
        // When
        Preco preco = Preco.of(valorPreco);
        
        // Then
        assertNotNull(preco);
        assertEquals(new BigDecimal("39.99"), preco.getValue());
    }

    @Test
    @DisplayName("Deve criar preço zero")
    void deveCriarPrecoZero() {
        // When
        Preco precoZero = Preco.zero();
        
        // Then
        assertNotNull(precoZero);
        assertEquals(BigDecimal.ZERO.setScale(2), precoZero.getValue());
        assertTrue(precoZero.ehZero());
    }

    @Test
    @DisplayName("Deve rejeitar preço negativo")
    void deveRejeitarPrecoNegativo() {
        // Given
        BigDecimal precoNegativo = new BigDecimal("-10.00");
        
        // When/Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            Preco.of(precoNegativo);
        });
        
        assertTrue(exception.getMessage().contains("não pode ser negativo"));
    }

    @Test
    @DisplayName("Deve rejeitar preço nulo")
    void deveRejeitarPrecoNulo() {
        // When/Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            Preco.of((BigDecimal) null);
        });
        
        assertTrue(exception.getMessage().contains("não pode ser nulo"));
    }

    @Test
    @DisplayName("Deve rejeitar string inválida")
    void deveRejeitarStringInvalida() {
        // Given
        String stringInvalida = "preço inválido";
        
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> {
            Preco.of(stringInvalida);
        });
    }

    @Test
    @DisplayName("Deve arredondar para 2 casas decimais")
    void deveArredondarParaDuasCasasDecimais() {
        // Given
        BigDecimal valorComMuitosPontos = new BigDecimal("19.999999");
        
        // When
        Preco preco = Preco.of(valorComMuitosPontos);
        
        // Then
        assertEquals(new BigDecimal("20.00"), preco.getValue());
        assertEquals(2, preco.getValue().scale());
    }

    @Test
    @DisplayName("Deve somar preços corretamente")
    void deveSomarPrecosCorretamente() {
        // Given
        Preco preco1 = Preco.of(new BigDecimal("10.50"));
        Preco preco2 = Preco.of(new BigDecimal("5.25"));
        
        // When
        Preco resultado = preco1.somar(preco2);
        
        // Then
        assertEquals(new BigDecimal("15.75"), resultado.getValue());
    }

    @Test
    @DisplayName("Deve subtrair preços corretamente")
    void deveSubtrairPrecosCorretamente() {
        // Given
        Preco preco1 = Preco.of(new BigDecimal("20.00"));
        Preco preco2 = Preco.of(new BigDecimal("5.50"));
        
        // When
        Preco resultado = preco1.subtrair(preco2);
        
        // Then
        assertEquals(new BigDecimal("14.50"), resultado.getValue());
    }

    @Test
    @DisplayName("Deve permitir subtração que resulta em negativo")
    void devePermitirSubtracaoQueResultaEmNegativo() {
        // Given
        Preco preco1 = Preco.of(new BigDecimal("5.00"));
        Preco preco2 = Preco.of(new BigDecimal("10.00"));
        
        // When
        Preco resultado = preco1.subtrair(preco2);
        
        // Then
        assertEquals(new BigDecimal("-5.00"), resultado.getValue());
    }

    @Test
    @DisplayName("Deve multiplicar preço por fator")
    void deveMultiplicarPrecoPorFator() {
        // Given
        Preco preco = Preco.of(new BigDecimal("10.00"));
        BigDecimal fator = new BigDecimal("2.5");
        
        // When
        Preco resultado = preco.multiplicar(fator);
        
        // Then
        assertEquals(new BigDecimal("25.00"), resultado.getValue());
    }

    @Test
    @DisplayName("Deve implementar equals corretamente")
    void deveImplementarEqualsCorretamente() {
        // Given
        Preco preco1 = Preco.of(new BigDecimal("19.99"));
        Preco preco2 = Preco.of(new BigDecimal("19.99"));
        Preco preco3 = Preco.of(new BigDecimal("29.99"));
        
        // When/Then
        assertEquals(preco1, preco2);
        assertNotEquals(preco1, preco3);
        assertEquals(preco1, preco1); // reflexivo
    }

    @Test
    @DisplayName("Deve implementar hashCode corretamente")
    void deveImplementarHashCodeCorretamente() {
        // Given
        Preco preco1 = Preco.of(new BigDecimal("19.99"));
        Preco preco2 = Preco.of(new BigDecimal("19.99"));
        
        // When/Then
        assertEquals(preco1.hashCode(), preco2.hashCode());
    }

    @Test
    @DisplayName("Deve implementar toString corretamente")
    void deveImplementarToStringCorretamente() {
        // Given
        Preco preco = Preco.of(new BigDecimal("19.99"));
        
        // When/Then
        assertEquals("19.99", preco.toString());
    }

    @Test
    @DisplayName("Deve detectar se é zero")
    void deveDetectarSeEhZero() {
        // Given
        Preco precoZero = Preco.zero();
        Preco precoNaoZero = Preco.of(new BigDecimal("1.00"));
        
        // When/Then
        assertTrue(precoZero.ehZero());
        assertFalse(precoNaoZero.ehZero());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "0.0", "0.00", "0.000"})
    @DisplayName("Deve reconhecer variações de zero")
    void deveReconhecerVariacoesDeZero(String valorZero) {
        // When
        Preco preco = Preco.of(valorZero);
        
        // Then
        assertTrue(preco.ehZero());
    }

    @Test
    @DisplayName("Deve tratar precisão em operações matemáticas")
    void deveTratarPrecisaoEmOperacoesMatematicas() {
        // Given
        Preco preco1 = Preco.of("10.33");
        Preco preco2 = Preco.of("10.33");
        
        // When
        Preco soma = preco1.somar(preco2);
        Preco multiplicacao = preco1.multiplicar(new BigDecimal("2"));
        
        // Then  
        assertEquals(new BigDecimal("20.66"), soma.getValue());
        assertEquals(new BigDecimal("20.66"), multiplicacao.getValue());
        assertEquals(soma, multiplicacao);
    }
}