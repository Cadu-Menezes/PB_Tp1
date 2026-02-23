package com.faculdade.produtos.model;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes baseados em propriedades para Preco usando Jqwik.
 * Verifica propriedades que devem ser sempre verdadeiras independente da entrada.
 */
@DisplayName("Testes Baseados em Propriedades do Preco")
class PrecoProperties {

    @Property
    @DisplayName("Preço válido deve sempre ter escala de 2 casas decimais")
    void precoValidoDeveSempreTerEscalaDeDuasCasasDecimais(@ForAll @BigRange(min = "0.00", max = "999999.99") BigDecimal valor) {
        // When
        Preco preco = Preco.of(valor);
        
        // Then
        assertEquals(2, preco.getValue().scale());
    }

    @Property
    @DisplayName("Soma de preços deve ser comutativa")
    void somaDePrecoDeveSerComutativa(
            @ForAll @BigRange(min = "0.00", max = "50000.00") BigDecimal valor1,
            @ForAll @BigRange(min = "0.00", max = "50000.00") BigDecimal valor2) {
        // Given
        Preco preco1 = Preco.of(valor1);
        Preco preco2 = Preco.of(valor2);
        
        // When
        Preco soma1 = preco1.somar(preco2);
        Preco soma2 = preco2.somar(preco1);
        
        // Then
        assertEquals(soma1, soma2);
    }

    @Property
    @DisplayName("Soma com zero deve ser elemento neutro")
    void somaComZeroDeveSerElementoNeutro(@ForAll @BigRange(min = "0.00", max = "999999.99") BigDecimal valor) {
        // Given
        Preco preco = Preco.of(valor);
        Preco zero = Preco.zero();
        
        // When
        Preco somaComZero = preco.somar(zero);
        
        // Then
        assertEquals(preco, somaComZero);
    }

    @Property
    @DisplayName("Subtração consigo mesmo deve resultar em zero")
    void subtracaoConsigoMesmoDeveResultarEmZero(@ForAll @BigRange(min = "0.00", max = "999999.99") BigDecimal valor) {
        // Given
        Preco preco = Preco.of(valor);
        
        // When
        Preco resultado = preco.subtrair(preco);
        
        // Then
        assertTrue(resultado.ehZero());
    }

    @Property
    @DisplayName("Multiplicação por 1 deve ser elemento neutro")
    void multiplicacaoPorUmDeveSerElementoNeutro(@ForAll @BigRange(min = "0.00", max = "999999.99") BigDecimal valor) {
        // Given
        Preco preco = Preco.of(valor);
        BigDecimal um = BigDecimal.ONE;
        
        // When
        Preco resultado = preco.multiplicar(um);
        
        // Then
        assertEquals(preco.getValue(), resultado.getValue());
    }

    @Property
    @DisplayName("Multiplicação por zero deve resultar em zero")
    void multiplicacaoPorZeroDeveResultarEmZero(@ForAll @BigRange(min = "0.00", max = "999999.99") BigDecimal valor) {
        // Given
        Preco preco = Preco.of(valor);
        BigDecimal zero = BigDecimal.ZERO;
        
        // When
        Preco resultado = preco.multiplicar(zero);
        
        // Then
        assertTrue(resultado.ehZero());
    }

    @Property
    @DisplayName("Equals deve ser reflexivo, simétrico e transitivo")
    void equalsDeveSerReflexivoSimetricoETransitivo(@ForAll @BigRange(min = "0.00", max = "999999.99") BigDecimal valor) {
        // Given
        Preco preco1 = Preco.of(valor);
        Preco preco2 = Preco.of(valor);
        Preco preco3 = Preco.of(valor);
        
        // Reflexivo
        assertEquals(preco1, preco1);
        
        // Simétrico
        assertEquals(preco1, preco2);
        assertEquals(preco2, preco1);
        
        // Transitivo
        assertEquals(preco1, preco2);
        assertEquals(preco2, preco3);
        assertEquals(preco1, preco3);
    }

    @Property
    @DisplayName("HashCode deve ser consistente")
    void hashCodeDeveSerConsistente(@ForAll @BigRange(min = "0.00", max = "999999.99") BigDecimal valor) {
        // Given
        Preco preco = Preco.of(valor);
        
        // When
        int hashCode1 = preco.hashCode();
        int hashCode2 = preco.hashCode();
        
        // Then
        assertEquals(hashCode1, hashCode2);
    }

    @Property
    @DisplayName("ToString deve representar valor corretamente")
    void toStringDeveRepresentarValorCorretamente(@ForAll @BigRange(min = "0.00", max = "999999.99") BigDecimal valor) {
        // Given
        Preco preco = Preco.of(valor);
        
        // When
        String toString = preco.toString();
        
        // Then
        assertEquals(preco.getValue().toString(), toString);
    }

    @Property
    @DisplayName("Preço deve detectar zero corretamente")
    void precoDeveDetectarZeroCorretamente(@ForAll @BigRange(min = "-999999.99", max = "999999.99") BigDecimal valor) {
        // Given
        Assume.that(valor.compareTo(BigDecimal.ZERO) >= 0); // Apenas valores não negativos
        
        // When
        Preco preco = Preco.of(valor);
        
        // Then
        assertEquals(valor.compareTo(BigDecimal.ZERO) == 0, preco.ehZero());
    }

    @Provide
    Arbitrary<BigDecimal> precosPequenos() {
        return Arbitraries.bigDecimals()
                .between(BigDecimal.ZERO, new BigDecimal("1000.00"))
                .ofScale(2);
    }

    @Property
    @DisplayName("Distributividade da multiplicação sobre adição")
    void distributividadeDaMultiplicacaoSobreAdicao(
            @ForAll("precosPequenos") BigDecimal a,
            @ForAll("precosPequenos") BigDecimal b,
            @ForAll @BigRange(min = "1.00", max = "10.00") BigDecimal fator) {
        // Given
        Preco precoA = Preco.of(a);
        Preco precoB = Preco.of(b);
        
        // When
        Preco somaMultiplicada = precoA.somar(precoB).multiplicar(fator);
        Preco multiplicacoesSomadas = precoA.multiplicar(fator).somar(precoB.multiplicar(fator));
        
        // Then
        assertEquals(somaMultiplicada.getValue(), multiplicacoesSomadas.getValue());
    }
}