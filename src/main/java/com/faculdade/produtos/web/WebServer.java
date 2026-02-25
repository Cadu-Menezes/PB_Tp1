package com.faculdade.produtos.web;

import com.faculdade.produtos.service.ProdutoService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.json.JsonMapper;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

/*
    Servidor web responsável por inicializar o Javalin,
    servir arquivos estáticos e registrar as rotas da API.
*/
public final class WebServer {

    private static final int PORTA_PADRAO = 7000;

    private final Javalin app;
    private final ProdutoController controller;

    public WebServer(ProdutoService produtoService) {
        this.controller = new ProdutoController(produtoService);
        this.app = configurarServidor();
        registrarRotas();
    }

    /*
        Configura o servidor Javalin com arquivos estáticos e CORS.
    */
    private Javalin configurarServidor() {
        // Configurar Gson como serializador JSON do Javalin
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonMapper gsonMapper = new JsonMapper() {
            @Override
            public @NotNull String toJsonString(@NotNull Object obj, @NotNull Type type) {
                return gson.toJson(obj, type);
            }

            @Override
            public <T> T fromJsonString(@NotNull String json, @NotNull Type targetType) {
                return gson.fromJson(json, targetType);
            }
        };

        return Javalin.create(config -> {
            config.jsonMapper(gsonMapper);

            // Servir arquivos estáticos da pasta resources/static na URL /static
            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/static";
                staticFiles.directory = "/static";
                staticFiles.location = Location.CLASSPATH;
            });

            // Habilitar CORS para desenvolvimento
            config.plugins.enableCors(cors -> cors.add(it -> it.anyHost()));
        });
    }

    /*
        Registra todas as rotas REST da aplicação.
    */
    private void registrarRotas() {
        // Redirecionar raiz para a página principal
        app.get("/", ctx -> ctx.redirect("/static/index.html"));

        // Rotas específicas ANTES das parametrizadas (evitar conflito com {id})
        app.get("/api/produtos/categoria/{categoria}", controller::listarPorCategoria);
        app.get("/api/produtos/estoque/baixo", controller::listarEstoqueBaixo);
        app.get("/api/categorias", controller::listarCategorias);
        app.get("/api/estatisticas", controller::obterEstatisticas);

        // Rotas parametrizadas por ID
        app.get("/api/produtos", controller::listarTodos);
        app.get("/api/produtos/{id}", controller::buscarPorId);

        // Rotas de comando (POST, PUT, DELETE)
        app.post("/api/produtos", controller::criarProduto);
        app.put("/api/produtos/{id}", controller::atualizarProduto);
        app.delete("/api/produtos/{id}", controller::excluirProduto);
        app.patch("/api/produtos/{id}/estoque", controller::atualizarEstoque);
    }

    /*
        Inicia o servidor na porta configurada.
    */
    public void iniciar() {
        app.start(PORTA_PADRAO);
        System.out.println("🌐 Servidor web iniciado em http://localhost:" + PORTA_PADRAO);
    }

    /*
        Para o servidor web.
    */
    public void parar() {
        app.stop();
        System.out.println("Servidor web encerrado.");
    }

    public int getPorta() {
        return PORTA_PADRAO;
    }
}
