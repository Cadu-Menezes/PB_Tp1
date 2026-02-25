package com.faculdade.produtos.web;

import com.faculdade.produtos.exception.*;
import com.faculdade.produtos.model.*;
import com.faculdade.produtos.service.ProdutoService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.javalin.http.Context;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
    Controller REST para operações de produto.
    Converte requisições HTTP em chamadas ao ProdutoService
    e formata as respostas em JSON.
*/
public final class ProdutoController {

    private final ProdutoService produtoService;
    private final Gson gson;
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /*
        Lista todos os produtos cadastrados.
        GET /api/produtos
    */
    public void listarTodos(Context ctx) {
        try {
            List<Produto> produtos = produtoService.obterTodosProdutos();
            List<Map<String, Object>> resposta = produtos.stream()
                    .map(this::produtoParaMap)
                    .collect(Collectors.toList());

            ctx.json(resposta);
        } catch (ExcecaoBancoDados e) {
            ctx.status(500).json(criarErro("Erro ao listar produtos", e.getMessage()));
        }
    }

    /*
        Busca um produto pelo ID.
        GET /api/produtos/{id}
    */
    public void buscarPorId(Context ctx) {
        try {
            String id = ctx.pathParam("id");
            ProdutoId produtoId = ProdutoId.of(id);
            Produto produto = produtoService.obterProduto(produtoId);

            ctx.json(produtoParaMap(produto));
        } catch (ProdutoNaoEncontradoException e) {
            ctx.status(404).json(criarErro("Produto não encontrado", e.getMessage()));
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(criarErro("ID inválido", e.getMessage()));
        } catch (ExcecaoBancoDados e) {
            ctx.status(500).json(criarErro("Erro interno", e.getMessage()));
        }
    }

    /*
        Cria um novo produto.
        POST /api/produtos
        Body: { nome, descricao, preco, categoria, quantidadeEstoque }
    */
    public void criarProduto(Context ctx) {
        try {
            JsonObject body = gson.fromJson(ctx.body(), JsonObject.class);

            Nome nome = Nome.of(obterString(body, "nome"));
            Descricao descricao = Descricao.of(obterString(body, "descricao"));
            Preco preco = Preco.of(new BigDecimal(obterString(body, "preco")));
            Categoria categoria = Categoria.deString(obterString(body, "categoria"));
            QuantidadeEstoque estoque = QuantidadeEstoque.of(Integer.parseInt(obterString(body, "quantidadeEstoque")));

            ProdutoId id = produtoService.criarProduto(nome, descricao, preco, categoria, estoque);

            Map<String, Object> resposta = new HashMap<>();
            resposta.put("id", id.getValue().toString());
            resposta.put("mensagem", "Produto criado com sucesso");

            ctx.status(201).json(resposta);

        } catch (ProdutoJaExisteException e) {
            ctx.status(409).json(criarErro("Produto já existe", e.getMessage()));
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(criarErro("Dados inválidos", e.getMessage()));
        } catch (ExcecaoBancoDados e) {
            ctx.status(500).json(criarErro("Erro interno", e.getMessage()));
        }
    }

    /*
        Atualiza um produto existente.
        PUT /api/produtos/{id}
        Body: { nome, descricao, preco, categoria, quantidadeEstoque }
    */
    public void atualizarProduto(Context ctx) {
        try {
            String id = ctx.pathParam("id");
            ProdutoId produtoId = ProdutoId.of(id);
            JsonObject body = gson.fromJson(ctx.body(), JsonObject.class);

            Nome nome = Nome.of(obterString(body, "nome"));
            Descricao descricao = Descricao.of(obterString(body, "descricao"));
            Preco preco = Preco.of(new BigDecimal(obterString(body, "preco")));
            Categoria categoria = Categoria.deString(obterString(body, "categoria"));
            QuantidadeEstoque estoque = QuantidadeEstoque.of(Integer.parseInt(obterString(body, "quantidadeEstoque")));

            produtoService.atualizarProduto(produtoId, nome, descricao, preco, categoria, estoque);

            Map<String, String> resposta = new HashMap<>();
            resposta.put("mensagem", "Produto atualizado com sucesso");

            ctx.json(resposta);

        } catch (ProdutoNaoEncontradoException e) {
            ctx.status(404).json(criarErro("Produto não encontrado", e.getMessage()));
        } catch (ProdutoJaExisteException e) {
            ctx.status(409).json(criarErro("Nome já existe", e.getMessage()));
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(criarErro("Dados inválidos", e.getMessage()));
        } catch (ExcecaoBancoDados e) {
            ctx.status(500).json(criarErro("Erro interno", e.getMessage()));
        }
    }

    /*
        Exclui um produto pelo ID.
        DELETE /api/produtos/{id}
    */
    public void excluirProduto(Context ctx) {
        try {
            String id = ctx.pathParam("id");
            ProdutoId produtoId = ProdutoId.of(id);
            produtoService.excluirProduto(produtoId);

            Map<String, String> resposta = new HashMap<>();
            resposta.put("mensagem", "Produto excluído com sucesso");

            ctx.json(resposta);

        } catch (ProdutoNaoEncontradoException e) {
            ctx.status(404).json(criarErro("Produto não encontrado", e.getMessage()));
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(criarErro("ID inválido", e.getMessage()));
        } catch (ExcecaoBancoDados e) {
            ctx.status(500).json(criarErro("Erro interno", e.getMessage()));
        }
    }

    /*
        Atualiza o estoque de um produto.
        PATCH /api/produtos/{id}/estoque
        Body: { quantidade }
    */
    public void atualizarEstoque(Context ctx) {
        try {
            String id = ctx.pathParam("id");
            ProdutoId produtoId = ProdutoId.of(id);
            JsonObject body = gson.fromJson(ctx.body(), JsonObject.class);

            int quantidade = body.get("quantidade").getAsInt();
            QuantidadeEstoque novaQuantidade = QuantidadeEstoque.of(quantidade);

            produtoService.atualizarEstoque(produtoId, novaQuantidade);

            Map<String, String> resposta = new HashMap<>();
            resposta.put("mensagem", "Estoque atualizado com sucesso");

            ctx.json(resposta);

        } catch (ProdutoNaoEncontradoException e) {
            ctx.status(404).json(criarErro("Produto não encontrado", e.getMessage()));
        } catch (IllegalArgumentException e) {
            ctx.status(400).json(criarErro("Dados inválidos", e.getMessage()));
        } catch (ExcecaoBancoDados e) {
            ctx.status(500).json(criarErro("Erro interno", e.getMessage()));
        }
    }

    /*
        Lista produtos por categoria.
        GET /api/produtos/categoria/{categoria}
    */
    public void listarPorCategoria(Context ctx) {
        try {
            String categoriaStr = ctx.pathParam("categoria");
            Categoria categoria = Categoria.deString(categoriaStr);
            List<Produto> produtos = produtoService.obterProdutosPorCategoria(categoria);

            List<Map<String, Object>> resposta = produtos.stream()
                    .map(this::produtoParaMap)
                    .collect(Collectors.toList());

            ctx.json(resposta);

        } catch (IllegalArgumentException e) {
            ctx.status(400).json(criarErro("Categoria inválida", e.getMessage()));
        } catch (ExcecaoBancoDados e) {
            ctx.status(500).json(criarErro("Erro interno", e.getMessage()));
        }
    }

    /*
        Lista produtos com estoque baixo.
        GET /api/produtos/estoque/baixo?limite=5
    */
    public void listarEstoqueBaixo(Context ctx) {
        try {
            int limite = Integer.parseInt(ctx.queryParam("limite") != null ? ctx.queryParam("limite") : "5");
            List<Produto> produtos = produtoService.obterProdutosComEstoqueBaixo(limite);

            List<Map<String, Object>> resposta = produtos.stream()
                    .map(this::produtoParaMap)
                    .collect(Collectors.toList());

            ctx.json(resposta);

        } catch (NumberFormatException e) {
            ctx.status(400).json(criarErro("Parâmetro inválido", "Limite deve ser um número inteiro"));
        } catch (ExcecaoBancoDados e) {
            ctx.status(500).json(criarErro("Erro interno", e.getMessage()));
        }
    }

    /*
        Lista todas as categorias disponíveis.
        GET /api/categorias
    */
    public void listarCategorias(Context ctx) {
        List<Map<String, String>> categorias = Arrays.stream(Categoria.values())
                .map(cat -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("valor", cat.name());
                    map.put("nome", cat.getNomeExibicao());
                    map.put("descricao", cat.getDescricao());
                    return map;
                })
                .collect(Collectors.toList());

        ctx.json(categorias);
    }

    /*
        Retorna estatísticas gerais dos produtos.
        GET /api/estatisticas
    */
    public void obterEstatisticas(Context ctx) {
        try {
            long total = produtoService.obterTotalDeProdutos();
            List<Produto> semEstoque = produtoService.obterProdutosSemEstoque();
            List<Produto> estoqueBaixo = produtoService.obterProdutosComEstoqueBaixo(5);

            // Filtrar apenas os que têm estoque entre 1 e 5 (excluir os sem estoque)
            long estoqueBaixoCount = estoqueBaixo.stream()
                    .filter(p -> p.getQuantidadeEstoque().getValue() > 0)
                    .count();

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalProdutos", total);
            stats.put("produtosSemEstoque", semEstoque.size());
            stats.put("produtosEstoqueBaixo", estoqueBaixoCount);

            ctx.json(stats);

        } catch (ExcecaoBancoDados e) {
            ctx.status(500).json(criarErro("Erro interno", e.getMessage()));
        }
    }

    // Métodos auxiliares

    /*
        Converte um Produto em Map para serialização JSON.
    */
    private Map<String, Object> produtoParaMap(Produto produto) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", produto.getId().getValue().toString());
        map.put("nome", produto.getNome().getValue());
        map.put("descricao", produto.getDescricao().getValue());
        map.put("preco", produto.getPreco().getValue());
        map.put("categoria", produto.getCategoria().name());
        map.put("categoriaNome", produto.getCategoria().getNomeExibicao());
        map.put("quantidadeEstoque", produto.getQuantidadeEstoque().getValue());
        map.put("criadoEm", produto.getCriadoEm().format(FORMATO_DATA));
        map.put("atualizadoEm", produto.getAtualizadoEm().format(FORMATO_DATA));
        return map;
    }

    /*
        Cria objeto de erro padronizado para respostas.
    */
    private Map<String, String> criarErro(String tipo, String mensagem) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", tipo);
        erro.put("mensagem", mensagem);
        return erro;
    }

    /*
        Extrai string de um JsonObject com validação.
    */
    private String obterString(JsonObject json, String campo) {
        if (!json.has(campo) || json.get(campo).isJsonNull()) {
            throw new IllegalArgumentException("Campo '" + campo + "' é obrigatório");
        }
        return json.get(campo).getAsString();
    }
}
