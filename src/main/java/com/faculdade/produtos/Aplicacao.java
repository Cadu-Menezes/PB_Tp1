package com.faculdade.produtos;

import com.faculdade.produtos.cli.ProdutoCLI;
import com.faculdade.produtos.config.DatabaseConfig;
import com.faculdade.produtos.config.DatabaseMigration;
import com.faculdade.produtos.repository.impl.RepositorioPostgreSqlProduto;
import com.faculdade.produtos.service.ProdutoService;

import javax.sql.DataSource;
import java.sql.SQLException;

/*
    Classe principal da aplicação.
    Responsável por inicializar o sistema e suas dependências.
*/
public final class Aplicacao {
    
    private static final String VERSAO_APP = "1.0.0";
    private static final String NOME_APP = "Sistema CRUD de Produtos";

    // Construtor privado para evitar instanciação
    private Aplicacao() {
        throw new AssertionError("Esta classe não deve ser instanciada");
    }

    public static void main(String[] args) {
        System.out.println(NOME_APP + " v" + VERSAO_APP);
        System.out.println("Inicializando sistema...");
        
        try {
            // Configurar e inicializar banco de dados
            DataSource dataSource = inicializarBancoDados();
            
            // Configurar dependências
            RepositorioPostgreSqlProduto repositorio = new RepositorioPostgreSqlProduto(dataSource);
            ProdutoService produtoService = new ProdutoService(repositorio, repositorio);
            
            // Inicializar CLI
            ProdutoCLI cli = new ProdutoCLI(produtoService);
            
            System.out.println("✅ Sistema inicializado com sucesso!");
            
            // Iniciar interface
            cli.iniciar();
            
        } catch (Exception e) {
            System.err.println("❌ Erro fatal ao inicializar sistema: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /*
        Inicializa e configura o banco de dados.
    */
    private static DataSource inicializarBancoDados() throws SQLException {
        System.out.println("Configurando banco de dados...");
        
        // Verificar se o banco existe, criar se necessário
        DatabaseConfig.ensureDatabaseExists();
        
        // Criar DataSource
        DataSource dataSource = DatabaseConfig.createDefaultDataSource();
        
        // Executar migrações
        DatabaseMigration.migrate(dataSource);
        
        System.out.println("✅ Banco de dados configurado.");
        
        return dataSource;
    }
}