package com.faculdade.produtos.cli;

import com.faculdade.produtos.exception.*;
import com.faculdade.produtos.model.*;
import com.faculdade.produtos.service.ProdutoService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

/*
    Interface de linha de comando para o sistema de produtos.
    Implementa padrão Command para operações do menu.
*/
public final class ProdutoCLI {
    
    private final ProdutoService produtoService;
    private final Scanner scanner;

    public ProdutoCLI(ProdutoService produtoService) {
        this.produtoService = produtoService;
        this.scanner = new Scanner(System.in);
    }

    /*
        Inicia a interface de linha de comando.
    */
    public void iniciar() {
        System.out.println("=== Sistema de Gerenciamento de Produtos ===");
        System.out.println("Bem-vindo!");
        
        boolean executando = true;
        
        while (executando) {
            try {
                exibirMenu();
                int escolha = lerEscolha();
                
                switch (escolha) {
                    case 1: processarCriarProduto(); break;
                    case 2: processarListarProdutos(); break;
                    case 3: processarBuscarProduto(); break;
                    case 4: processarAtualizarProduto(); break;
                    case 5: processarExcluirProduto(); break;
                    case 6: processarAtualizarEstoque(); break;
                    case 7: processarListarPorCategoria(); break;
                    case 8: processarRelatorioEstoqueBaixo(); break;
                    case 9: processarEstatisticas(); break;
                    case 0: 
                        executando = false;
                        System.out.println("Obrigado por usar o sistema!");
                        break;
                    default:
                        System.out.println("Opção inválida. Tente novamente.");
                }
                
            } catch (Exception e) {
                tratarErro(e);
            }
            
            if (executando) {
                System.out.println("\\nPressione Enter para continuar...");
                scanner.nextLine();
            }
        }
        
        scanner.close();
    }

    private void exibirMenu() {
        System.out.println();
        System.out.println("=== MENU PRINCIPAL ===");
        System.out.println("1. Criar produto");
        System.out.println("2. Listar todos os produtos");
        System.out.println("3. Buscar produto");
        System.out.println("4. Atualizar produto");
        System.out.println("5. Excluir produto");
        System.out.println("6. Gerenciar estoque");
        System.out.println("7. Listar por categoria");
        System.out.println("8. Relatório de estoque baixo");
        System.out.println("9. Estatísticas");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opção: ");
    }

    private int lerEscolha() {
        try {
            int escolha = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            return escolha;
        } catch (Exception e) {
            scanner.nextLine(); // Clear invalid input
            return -1;
        }
    }

    private void processarCriarProduto() {
        System.out.println("\\n=== CRIAR PRODUTO ===");
        
        try {
            Nome nome = lerNomeProduto();
            Descricao descricao = lerDescricaoProduto();
            Preco preco = lerPrecoProduto();
            Categoria categoria = lerCategoriaProduto();
            QuantidadeEstoque estoque = lerQuantidadeEstoque();
            
            ProdutoId produtoId = produtoService.criarProduto(nome, descricao, preco, categoria, estoque);
            Produto produto = produtoService.obterProduto(produtoId);
            
            System.out.println("\\n✅ Produto criado com sucesso!");
            exibirDetalhesProduto(produto);
            
        } catch (ProdutoJaExisteException e) {
            System.err.println("❌ Erro: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Erro inesperado ao criar produto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void processarListarProdutos() {
        System.out.println("\\n=== LISTAR PRODUTOS ===");
        
        try {
            List<Produto> produtos = produtoService.obterTodosProdutos();
            
            if (produtos.isEmpty()) {
                System.out.println("Nenhum produto encontrado.");
                return;
            }
            
            System.out.println("Total de produtos: " + produtos.size());
            System.out.println();
            
            for (Produto produto : produtos) {
                exibirDetalhesProduto(produto);
                System.out.println("─".repeat(50));
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao listar produtos: " + e.getMessage());
        }
    }

    private void processarBuscarProduto() {
        System.out.println("\\n=== BUSCAR PRODUTO ===");
        System.out.println("1. Buscar por ID");
        System.out.println("2. Buscar por nome");
        System.out.print("Escolha o tipo de busca: ");
        
        try {
            int tipoBusca = scanner.nextInt();
            scanner.nextLine();
            
            switch (tipoBusca) {
                case 1:
                    buscarPorId();
                    break;
                case 2:
                    buscarPorNome();
                    break;
                default:
                    System.out.println("Tipo de busca inválido.");
            }
            
        } catch (Exception e) {
            scanner.nextLine();
            System.err.println("❌ Erro na busca: " + e.getMessage());
        }
    }

    private void processarAtualizarProduto() {
        System.out.println("\\n=== ATUALIZAR PRODUTO ===");
        
        try {
            System.out.print("Digite o ID do produto a atualizar: ");
            String idTexto = scanner.nextLine();
            ProdutoId produtoId = ProdutoId.of(idTexto);
            
            // Buscar produto existente
            Produto produtoExistente = produtoService.obterProduto(produtoId);
            
            System.out.println("\\nProduto encontrado:");
            exibirDetalhesProduto(produtoExistente);
            
            System.out.println("\\nDigite os novos valores (pressione Enter para manter o valor atual):");
            
            Nome novoNome = lerNomeProdutoOpcional(produtoExistente.getNome());
            Descricao novaDescricao = lerDescricaoProdutoOpcional(produtoExistente.getDescricao());
            Preco novoPreco = lerPrecoProdutoOpcional(produtoExistente.getPreco());
            Categoria novaCategoria = lerCategoriaProdutoOpcional(produtoExistente.getCategoria());
            QuantidadeEstoque novoEstoque = lerQuantidadeEstoqueOpcional(produtoExistente.getQuantidadeEstoque());
            
            produtoService.atualizarProduto(
                produtoId, novoNome, novaDescricao, novoPreco, novaCategoria, novoEstoque);
            
            System.out.println("\\n✅ Produto atualizado com sucesso!");
            // Buscar produto atualizado para exibição
            Produto produtoAtualizado = produtoService.obterProduto(produtoId);
            exibirDetalhesProduto(produtoAtualizado);
            
        } catch (ProdutoNaoEncontradoException e) {
            System.err.println("❌ Produto não encontrado: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Erro ao atualizar produto: " + e.getMessage());
        }
    }

    private void processarExcluirProduto() {
        System.out.println("\\n=== EXCLUIR PRODUTO ===");
        
        try {
            System.out.print("Digite o ID do produto a excluir: ");
            String idTexto = scanner.nextLine();
            ProdutoId produtoId = ProdutoId.of(idTexto);
            
            // Buscar e exibir produto antes de excluir
            Produto produto = produtoService.obterProduto(produtoId);
            
            System.out.println("\\nProduto a ser excluído:");
            exibirDetalhesProduto(produto);
            
            System.out.print("\\nTem certeza que deseja excluir este produto? (s/N): ");
            String confirmacao = scanner.nextLine().toLowerCase();
            
            if ("s".equals(confirmacao) || "sim".equals(confirmacao)) {
                produtoService.excluirProduto(produtoId);
                System.out.println("\\n✅ Produto excluído com sucesso!");
            } else {
                System.out.println("\\n❌ Operação cancelada.");
            }
            
        } catch (ProdutoNaoEncontradoException e) {
            System.err.println("❌ Produto não encontrado: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Erro ao excluir produto: " + e.getMessage());
        }
    }

    // Métodos auxiliares para leitura de entrada
    
    private Nome lerNomeProduto() {
        System.out.print("Nome do produto: ");
        String nomeTexto = scanner.nextLine();
        return Nome.of(nomeTexto);
    }
    
    private Nome lerNomeProdutoOpcional(Nome valorAtual) {
        System.out.printf("Nome (%s): ", valorAtual.getValue());
        String entrada = scanner.nextLine().trim();
        return entrada.isEmpty() ? valorAtual : Nome.of(entrada);
    }

    private Descricao lerDescricaoProduto() {
        System.out.print("Descrição: ");
        String descricaoTexto = scanner.nextLine();
        return Descricao.of(descricaoTexto);
    }
    
    private Descricao lerDescricaoProdutoOpcional(Descricao valorAtual) {
        System.out.printf("Descrição (%s): ", valorAtual.getValue());
        String entrada = scanner.nextLine().trim();
        return entrada.isEmpty() ? valorAtual : Descricao.of(entrada);
    }

    private Preco lerPrecoProduto() {
        while (true) {
            try {
                System.out.print("Preço: R$ ");
                BigDecimal precoValor = scanner.nextBigDecimal();
                scanner.nextLine();
                return Preco.of(precoValor);
            } catch (Exception e) {
                System.err.println("❌ Preço inválido. Digite um valor numérico válido.");
                scanner.nextLine();
            }
        }
    }
    
    private Preco lerPrecoProdutoOpcional(Preco valorAtual) {
        while (true) {
            try {
                System.out.printf("Preço (R$ %s): ", valorAtual.getValue());
                String entrada = scanner.nextLine().trim();
                if (entrada.isEmpty()) {
                    return valorAtual;
                }
                BigDecimal precoValor = new BigDecimal(entrada);
                return Preco.of(precoValor);
            } catch (Exception e) {
                System.err.println("❌ Preço inválido. Digite um valor numérico válido.");
            }
        }
    }

    private Categoria lerCategoriaProduto() {
        while (true) {
            try {
                System.out.println("\\nCategorias disponíveis:");
                Categoria[] categorias = Categoria.values();
                for (int i = 0; i < categorias.length; i++) {
                    System.out.printf("%d. %s\\n", i + 1, categorias[i].formatarParaExibicao());
                }
                
                System.out.print("Escolha a categoria (1-" + categorias.length + "): ");
                int escolha = scanner.nextInt();
                scanner.nextLine();
                
                if (escolha >= 1 && escolha <= categorias.length) {
                    return categorias[escolha - 1];
                }
                
                System.err.println("❌ Categoria inválida. Tente novamente.");
                
            } catch (Exception e) {
                System.err.println("❌ Entrada inválida. Digite um número.");
                scanner.nextLine();
            }
        }
    }
    
    private Categoria lerCategoriaProdutoOpcional(Categoria valorAtual) {
        while (true) {
            try {
                System.out.println("\\nCategorias disponíveis:");
                Categoria[] categorias = Categoria.values();
                for (int i = 0; i < categorias.length; i++) {
                    String marcador = categorias[i].equals(valorAtual) ? " (atual)" : "";
                    System.out.printf("%d. %s%s\\n", i + 1, categorias[i].formatarParaExibicao(), marcador);
                }
                
                System.out.printf("Escolha a categoria (1-%d) ou Enter para manter atual: ", categorias.length);
                String entrada = scanner.nextLine().trim();
                
                if (entrada.isEmpty()) {
                    return valorAtual;
                }
                
                int escolha = Integer.parseInt(entrada);
                if (escolha >= 1 && escolha <= categorias.length) {
                    return categorias[escolha - 1];
                }
                
                System.err.println("❌ Categoria inválida. Tente novamente.");
                
            } catch (Exception e) {
                System.err.println("❌ Entrada inválida. Digite um número ou pressione Enter.");
            }
        }
    }

    private QuantidadeEstoque lerQuantidadeEstoque() {
        while (true) {
            try {
                System.out.print("Quantidade em estoque: ");
                int quantidade = scanner.nextInt();
                scanner.nextLine();
                return QuantidadeEstoque.of(quantidade);
            } catch (Exception e) {
                System.err.println("❌ Quantidade inválida. Digite um número válido.");
                scanner.nextLine();
            }
        }
    }
    
    private QuantidadeEstoque lerQuantidadeEstoqueOpcional(QuantidadeEstoque valorAtual) {
        while (true) {
            try {
                System.out.printf("Quantidade em estoque (%d): ", valorAtual.getValue());
                String entrada = scanner.nextLine().trim();
                if (entrada.isEmpty()) {
                    return valorAtual;
                }
                int quantidade = Integer.parseInt(entrada);
                return QuantidadeEstoque.of(quantidade);
            } catch (Exception e) {
                System.err.println("❌ Quantidade inválida. Digite um número válido.");
            }
        }
    }

    // Métodos de exibição e utilidade
    
    private void exibirDetalhesProduto(Produto produto) {
        System.out.printf("ID: %s\\n", produto.getId());
        System.out.printf("Nome: %s\\n", produto.getNome());
        System.out.printf("Descrição: %s\\n", produto.getDescricao());
        System.out.printf("Preço: R$ %s\\n", produto.getPreco());
        System.out.printf("Categoria: %s\\n", produto.getCategoria().formatarParaExibicao());
        System.out.printf("Estoque: %d unidades\\n", produto.getQuantidadeEstoque().getValue());
        System.out.printf("Criado em: %s\\n", produto.getCriadoEm());
        System.out.printf("Atualizado em: %s\\n", produto.getAtualizadoEm());
    }

    private void tratarErro(Exception e) {
        if (e instanceof ExcecaoProduto) {
            ExcecaoProduto ep = (ExcecaoProduto) e;
            System.err.printf("❌ Erro [%s]: %s\\n", ep.obterCodigoErro(), e.getMessage());
        } else {
            System.err.println("❌ Erro inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Métodos auxiliares para operação de busca
    
    private void buscarPorId() {
        try {
            System.out.print("Digite o ID do produto: ");
            String idTexto = scanner.nextLine();
            ProdutoId produtoId = ProdutoId.of(idTexto);
            
            Produto produto = produtoService.obterProduto(produtoId);
            
            System.out.println("\\nProduto encontrado:");
            exibirDetalhesProduto(produto);
            
        } catch (ProdutoNaoEncontradoException e) {
            System.err.println("❌ Produto não encontrado: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Erro na busca por ID: " + e.getMessage());
        }
    }

    private void buscarPorNome() {
        try {
            System.out.print("Digite o nome do produto: ");
            String nomeTexto = scanner.nextLine();
            Nome nome = Nome.of(nomeTexto);
            
            Produto produto = produtoService.obterProduto(nome);
            
            System.out.println("\\nProduto encontrado:");
            exibirDetalhesProduto(produto);
            
        } catch (ProdutoNaoEncontradoException e) {
            System.err.println("❌ Produto não encontrado: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Erro na busca por nome: " + e.getMessage());
        }
    }

    private void processarAtualizarEstoque() {
        System.out.println("\\n=== GERENCIAR ESTOQUE ===");
        
        // Implementação simplificada - pode ser expandida
        System.out.println("Funcionalidade de atualização de estoque será implementada em versão futura.");
    }

    private void processarListarPorCategoria() {
        System.out.println("\\n=== LISTAR POR CATEGORIA ===");
        
        // Implementação simplificada - pode ser expandida  
        System.out.println("Funcionalidade de listagem por categoria será implementada em versão futura.");
    }

    private void processarRelatorioEstoqueBaixo() {
        System.out.println("\\n=== RELATÓRIO DE ESTOQUE BAIXO ===");
        
        // Implementação simplificada - pode ser expandida
        System.out.println("Relatório de estoque baixo será implementado em versão futura.");
    }

    private void processarEstatisticas() {
        System.out.println("\\n=== ESTATÍSTICAS ===");
        
        try {
            List<Produto> produtos = produtoService.obterTodosProdutos();
            
            System.out.printf("Total de produtos: %d\\n", produtos.size());
            
            // Implementação simplificada - pode ser expandida com mais estatísticas
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao obter estatísticas: " + e.getMessage());
        }
    }
}