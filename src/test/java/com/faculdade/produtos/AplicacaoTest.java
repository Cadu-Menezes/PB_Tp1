package com.faculdade.produtos;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes da Aplicação")
class AplicacaoTest {

    @Test
    @DisplayName("Aplicacao deve ter construtor privado")
    void deveTerConstrutorPrivado() throws NoSuchMethodException {
        // Given/When
        Constructor<Aplicacao> construtor = Aplicacao.class.getDeclaredConstructor();
        
        // Then
        assertTrue(java.lang.reflect.Modifier.isPrivate(construtor.getModifiers()));
    }

    @Test
    @DisplayName("Método main deve existir e ser público estático")
    void deveTerMetodoMainPublicoEstatico() {
        try {
            // Given/When
            Method metodoMain = Aplicacao.class.getDeclaredMethod("main", String[].class);
            
            // Then
            assertTrue(java.lang.reflect.Modifier.isPublic(metodoMain.getModifiers()));
            assertTrue(java.lang.reflect.Modifier.isStatic(metodoMain.getModifiers()));
            assertEquals("main", metodoMain.getName());
            assertEquals(void.class, metodoMain.getReturnType());
        } catch (NoSuchMethodException e) {
            fail("Método main não encontrado");
        }
    }

    @Test
    @DisplayName("Método inicializarBancoDados deve existir e ser privado")
    void deveTerMetodoInicializarBancoDadosPrivado() {
        try {
            // Given/When
            Method metodo = Aplicacao.class.getDeclaredMethod("inicializarBancoDados");
            
            // Then
            assertTrue(java.lang.reflect.Modifier.isPrivate(metodo.getModifiers()));
            assertTrue(java.lang.reflect.Modifier.isStatic(metodo.getModifiers()));
            assertEquals("inicializarBancoDados", metodo.getName());
        } catch (NoSuchMethodException e) {
            fail("Método inicializarBancoDados não encontrado");
        }
    }

    @Test
    @DisplayName("Classe Aplicacao deve estar no package raiz correto")
    void deveEstarNoPackageRaizCorreto() {
        // Given/When
        String nomePackage = Aplicacao.class.getPackage().getName();
        
        // Then
        assertEquals("com.faculdade.produtos", nomePackage);
    }

    @Test
    @DisplayName("Classe Aplicacao deve ser final")
    void deveSerClasseFinal() {
        // Given/When
        boolean ehFinal = java.lang.reflect.Modifier.isFinal(Aplicacao.class.getModifiers());
        
        // Then
        assertTrue(ehFinal, "Classe Aplicacao deve ser final");
    }
}