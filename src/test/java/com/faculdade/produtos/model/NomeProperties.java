package com.faculdade.produtos.model;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/*
    Testes baseados em propriedades para Nome usando Jqwik.
    Verifica propriedades que devem ser sempre verdadeiras independente da entrada.
*/
@DisplayName("Testes Baseados em Propriedades do Nome")
class NomeProperties {

    @Property
    @DisplayName("Nome válido deve sempre retornar valor normalizado sem espaços nas extremidades")
    void nomeValidoDeveSempreRetornarValorNormalizadoSemEspacosNasExtremidades(@ForAll @StringLength(min = 1, max = 100) @AlphaChars String entrada) {
        // Given
        String entradaComEspacos = "  " + entrada + "  ";
        
        // When
        Nome nome = Nome.of(entradaComEspacos);
        
        // Then
        assertEquals(entrada, nome.getValue());
        assertFalse(nome.getValue().startsWith(" "));
        assertFalse(nome.getValue().endsWith(" "));
    }

    @Property
    @DisplayName("Nomes com mesma string (ignorando espaços) devem ser iguais")
    void nomesComMesmaStringIgnorandoEspacosDevemSerIguais(@ForAll @StringLength(min = 1, max = 100) @AlphaChars String entrada) {
        // Given
        String entrada1 = "  " + entrada + "  ";
        String entrada2 = " " + entrada + " ";
        
        // When
        Nome nome1 = Nome.of(entrada1);
        Nome nome2 = Nome.of(entrada2);
        
        // Then
        assertEquals(nome1, nome2);
        assertEquals(nome1.hashCode(), nome2.hashCode());
    }

    @Property
    @DisplayName("toString deve sempre retornar o mesmo valor que getValue")
    void toStringDeveSempreRetornarMesmoValorQueGetValue(@ForAll @StringLength(min = 1, max = 100) String entrada) {
        // Given
        Assume.that(!entrada.trim().isEmpty());
        
        // When
        Nome nome = Nome.of(entrada);
        
        // Then
        assertEquals(nome.getValue(), nome.toString());
    }

    @Property
    @DisplayName("Criação de nome deve ser idempotente")
    void criacaoDeNomeDeveSerIdempotente(@ForAll @StringLength(min = 1, max = 100) @AlphaChars String entrada) {
        // When
        Nome nome1 = Nome.of(entrada);
        Nome nome2 = Nome.of(nome1.getValue());
        
        // Then
        assertEquals(nome1, nome2);
        assertEquals(nome1.getValue(), nome2.getValue());
    }

    @Property
    @DisplayName("Nomes válidos devem sempre ter comprimento entre 1 e 100 caracteres")
    void nomesValidosDevemSempreTerComprimentoEntre1E100Caracteres(@ForAll @StringLength(min = 1, max = 100) @AlphaChars String entrada) {
        // When
        Nome nome = Nome.of(entrada);
        
        // Then
        int comprimento = nome.getValue().length();
        assertTrue(comprimento >= 1);
        assertTrue(comprimento <= 100);
    }

    @Property
    @DisplayName("Equals deve ser reflexivo, simétrico e transitivo")
    void equalsDeveSerReflexivoSimetricoETransitivo(@ForAll @StringLength(min = 1, max = 50) @AlphaChars String entrada) {
        // Given
        Nome nome1 = Nome.of(entrada);
        Nome nome2 = Nome.of(entrada);
        Nome nome3 = Nome.of(entrada);
        
        // Reflexivo
        assertEquals(nome1, nome1);
        
        // Simétrico
        assertEquals(nome1, nome2);
        assertEquals(nome2, nome1);
        
        // Transitivo
        assertEquals(nome1, nome2);
        assertEquals(nome2, nome3);
        assertEquals(nome1, nome3);
    }

    @Property
    @DisplayName("HashCode deve ser consistente")
    void hashCodeDeveSerConsistente(@ForAll @StringLength(min = 1, max = 100) @AlphaChars String entrada) {
        // Given
        Nome nome = Nome.of(entrada);
        
        // When
        int hashCode1 = nome.hashCode();
        int hashCode2 = nome.hashCode();
        
        // Then
        assertEquals(hashCode1, hashCode2);
    }
}