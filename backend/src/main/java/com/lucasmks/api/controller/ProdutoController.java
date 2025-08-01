package com.lucasmks.api.controller;

import static spark.Spark.*;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lucasmks.api.dto.ApiResponse;
import com.lucasmks.api.dto.EditarProdutoRequest;
import com.lucasmks.api.dto.EstoqueRequest;
import com.lucasmks.api.dto.ProdutoRequest;
import com.lucasmks.api.dto.ProdutoResponse;
import com.lucasmks.api.mapper.ProdutoApiMapper;
import com.lucasmks.domain.exception.ProdutoJaExistenteException;
import com.lucasmks.domain.exception.ProdutoNaoEncontradoException;
import com.lucasmks.domain.exception.ValidacaoProdutoException;
import com.lucasmks.domain.model.Produto;
import com.lucasmks.domain.usecase.AdicionarEstoqueUseCase;
import com.lucasmks.domain.usecase.BuscarProdutoUseCase;
import com.lucasmks.domain.usecase.CadastrarProdutoUseCase;
import com.lucasmks.domain.usecase.DeletarProdutoUseCase;
import com.lucasmks.domain.usecase.EditarProdutoUseCase;
import com.lucasmks.domain.usecase.ListarTodosProdutosUseCase;
import com.lucasmks.domain.usecase.RemoverEstoqueUseCase;
import com.lucasmks.infrastructure.factory.ApplicationFactory;

public class ProdutoController {
    
    private final ObjectMapper objectMapper;
    private final CadastrarProdutoUseCase cadastrarProdutoUseCase;
    private final BuscarProdutoUseCase buscarProdutoUseCase;
    private final AdicionarEstoqueUseCase adicionarEstoqueUseCase;
    private final RemoverEstoqueUseCase removerEstoqueUseCase;
    private final ListarTodosProdutosUseCase listarTodosProdutosUseCase;
    private final EditarProdutoUseCase editarProdutoUseCase;
    private final DeletarProdutoUseCase deletarProdutoUseCase;
    
    public ProdutoController() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // Para suporte a LocalDateTime

        this.cadastrarProdutoUseCase = ApplicationFactory.getCadastrarProdutoUseCase();
        this.buscarProdutoUseCase = ApplicationFactory.getBuscarProdutoUseCase();
        this.adicionarEstoqueUseCase = ApplicationFactory.getAdicionarEstoqueUseCase();
        this.removerEstoqueUseCase = ApplicationFactory.getRemoverEstoqueUseCase();
        this.listarTodosProdutosUseCase = ApplicationFactory.getListarTodosProdutosUseCase();
        this.editarProdutoUseCase = ApplicationFactory.getEditarProdutoUseCase();
        this.deletarProdutoUseCase = ApplicationFactory.getDeletarProdutoUseCase();
    }

    
    public void setupRoutes() {
               
        // Configurar content type padrão
        before((request, response) -> {
            response.type("application/json");
        });

        // GET /api/produtos - Listar todos os produtos
        get("/api/produtos", this::listarProdutos);
        
        // GET /api/produtos/{codigoBarras} - Buscar produto
        get("/api/produtos/:codigoBarras", this::buscarProduto);
        
        // POST /api/produtos - Cadastrar produto
        post("/api/produtos", this::cadastrarProduto);

        // PUT /api/produtos/:codigoBarras - Editar produto
        put("/api/produtos/:codigoBarras", this::editarProduto);

        // DELETE /api/produtos/:codigoBarras - Deletar produto
        delete("/api/produtos/:codigoBarras", this::deletarProduto);

        // PUT /api/produtos/:codigoBarras/adicionarEstoque - Adicionar estoque
        put("/api/produtos/:codigoBarras/adicionarEstoque", this::adicionarEstoque); // <-- Nova rota

        // PUT /api/produtos/:codigoBarras/removerEstoque - Remover estoque
        put("/api/produtos/:codigoBarras/removerEstoque", this::removerEstoque); // <-- Nova rota

    
        exception(ProdutoNaoEncontradoException.class, (exception, request, response) -> {
            response.status(404); // Status 404 para produto não encontrado
            response.body(toJson(ApiResponse.error(exception.getMessage())));
            // Nao logar stack trace completo para 404s esperados, mas logar mensagem
            // logger.warn("Produto não encontrado na rota {}: {}", request.pathInfo(), exception.getMessage());
        });

        exception(ValidacaoProdutoException.class, (exception, request, response) -> {
            response.status(400); // Status 400 para erros de validação de input
            response.body(toJson(ApiResponse.error(exception.getMessage())));
            // logger.warn("Erro de validação na rota {}: {}", request.pathInfo(), exception.getMessage());
        });

        exception(ProdutoJaExistenteException.class, (exception, request, response) -> {
            response.status(409); // Status 409 Conflict para recurso existente
            response.body(toJson(ApiResponse.error(exception.getMessage())));
            // logger.warn("Conflito na rota {}: {}", request.pathInfo(), exception.getMessage());
        });

        // Exception handler (opcional: mover para classe utilitária de erros global)
        exception(Exception.class, (exception, request, response) -> {
            response.status(500);
            // Logger da exceção
            // logger.error("Erro interno do servidor: {}", exception.getMessage(), exception);
            response.body(toJson(ApiResponse.error("Erro interno do servidor: " + exception.getMessage())));
            exception.printStackTrace(); // Apenas para debug em desenvolvimento, remover em produção
        });
    }

    private Object listarProdutos(spark.Request request, spark.Response response) {
        try {
            List<Produto> produtos = listarTodosProdutosUseCase.executar();
            List<ProdutoResponse> produtosResponse = ProdutoApiMapper.toResponseList(produtos); // Assegure-se de ter um toResponseList no seu ProdutoApiMapper
            response.status(200);
            return toJson(ApiResponse.success("Lista de produtos carregada com sucesso", produtosResponse));
        } catch (Exception e) {
            response.status(500);
            return toJson(ApiResponse.error("Erro ao listar produtos: " + e.getMessage()));
        }
    }
    
    private Object buscarProduto(spark.Request request, spark.Response response) {
        try {
            String codigoBarras = request.params(":codigoBarras");
            
            if (codigoBarras == null || codigoBarras.trim().isEmpty()) {
                response.status(400);
                return toJson(ApiResponse.error("Código de barras é obrigatório"));
            }
            
            Produto produto = buscarProdutoUseCase.executar(codigoBarras);
            
            if (produto != null) {
                ProdutoResponse produtoResponse = ProdutoApiMapper.toResponse(produto);
                response.status(200);
                return toJson(ApiResponse.success("Produto encontrado", produtoResponse));
            } else {
                response.status(404);
                return toJson(ApiResponse.error("Produto não encontrado"));
            }
            
        } catch (Exception e) {
            response.status(500);
            return toJson(ApiResponse.error("Erro ao buscar produto: " + e.getMessage()));
        }
    }
    
    private Object cadastrarProduto(spark.Request request, spark.Response response) {
        try {
            String body = request.body();
            
            if (body == null || body.trim().isEmpty()) {
                response.status(400);
                return toJson(ApiResponse.error("Corpo da requisição é obrigatório"));
            }
            
            ProdutoRequest produtoRequest = objectMapper.readValue(body, ProdutoRequest.class);
            
            // Validações básicas
            if (produtoRequest.getCodigoBarras() == null || produtoRequest.getCodigoBarras().trim().isEmpty()) {
                response.status(400);
                return toJson(ApiResponse.error("Código de barras é obrigatório"));
            }
            
            if (produtoRequest.getNome() == null || produtoRequest.getNome().trim().isEmpty()) {
                response.status(400);
                return toJson(ApiResponse.error("Nome é obrigatório"));
            }
            
            Produto produto = ProdutoApiMapper.toDomain(produtoRequest);
            cadastrarProdutoUseCase.executar(produto);
            
            ProdutoResponse produtoResponse = ProdutoApiMapper.toResponse(produto);
            response.status(201);
            return toJson(ApiResponse.success("Produto cadastrado com sucesso", produtoResponse));
            
        } catch (Exception e) {
            response.status(500);
            return toJson(ApiResponse.error("Erro ao cadastrar produto: " + e.getMessage()));
        }
    }

    private Object adicionarEstoque(spark.Request request, spark.Response response) {
        try {
            String codigoBarras = request.params(":codigoBarras");
            EstoqueRequest estoqueRequest = objectMapper.readValue(request.body(), EstoqueRequest.class);

            // Validações básicas de input HTTP
            if (codigoBarras == null || codigoBarras.trim().isEmpty()) {
                response.status(400);
                return toJson(ApiResponse.error("Código de barras é obrigatório."));
            }
            if (estoqueRequest.getQuantidade() <= 0) {
                response.status(400);
                return toJson(ApiResponse.error("Quantidade a adicionar deve ser maior que zero."));
            }
            if (estoqueRequest.getMotivo() == null || estoqueRequest.getMotivo().trim().isEmpty()) {
                response.status(400);
                return toJson(ApiResponse.error("Motivo da adição de estoque é obrigatório."));
            }

            adicionarEstoqueUseCase.executar(codigoBarras, estoqueRequest.getQuantidade(), estoqueRequest.getMotivo());

            response.status(200);
            return toJson(ApiResponse.success("Estoque adicionado com sucesso para o produto " + codigoBarras, null));

        } catch (Exception e) {
            response.status(500); // Erros de negócio específicos (ProdutoNaoEncontrado, ValidacaoProduto) poderiam ter status diferentes
            return toJson(ApiResponse.error("Erro ao adicionar estoque: " + e.getMessage()));
        }
    }

    private Object removerEstoque(spark.Request request, spark.Response response) {
        try {
            String codigoBarras = request.params(":codigoBarras");
            EstoqueRequest estoqueRequest = objectMapper.readValue(request.body(), EstoqueRequest.class);

            // Validações básicas de input HTTP
            if (codigoBarras == null || codigoBarras.trim().isEmpty()) {
                response.status(400);
                return toJson(ApiResponse.error("Código de barras é obrigatório."));
            }
            if (estoqueRequest.getQuantidade() <= 0) {
                response.status(400);
                return toJson(ApiResponse.error("Quantidade a remover deve ser maior que zero."));
            }
            if (estoqueRequest.getMotivo() == null || estoqueRequest.getMotivo().trim().isEmpty()) {
                response.status(400);
                return toJson(ApiResponse.error("Motivo da remoção de estoque é obrigatório."));
            }

            removerEstoqueUseCase.executar(codigoBarras, estoqueRequest.getQuantidade(), estoqueRequest.getMotivo());

            response.status(200);
            return toJson(ApiResponse.success("Estoque removido com sucesso para o produto " + codigoBarras, null));

        } catch (Exception e) {
            response.status(500); // Erros de negócio específicos (ProdutoNaoEncontrado, EstoqueInsuficiente, ValidacaoProduto) poderiam ter status diferentes
            return toJson(ApiResponse.error("Erro ao remover estoque: " + e.getMessage()));
        }
    }

    public Object editarProduto(spark.Request request, spark.Response response) {
        try {
            String codigoBarras = request.params(":codigoBarras");
            if (codigoBarras == null || codigoBarras.trim().isEmpty()) {
                response.status(400);
                return toJson(ApiResponse.error("Código de barras é obrigatório"));
            }

            String body = request.body();
            if (body == null || body.trim().isEmpty()) {
                response.status(400);
                return toJson(ApiResponse.error("Corpo da requisição é obrigatório"));
            }
            
            EditarProdutoRequest editarRequest = objectMapper.readValue(body, EditarProdutoRequest.class);
            
            // Validações básicas
            if (editarRequest.getNome() == null || editarRequest.getNome().trim().isEmpty()) {
                response.status(400);
                return toJson(ApiResponse.error("Nome é obrigatório"));
            }
            
            if (editarRequest.getCategoria() == null || editarRequest.getCategoria().trim().isEmpty()) {
                response.status(400);
                return toJson(ApiResponse.error("Categoria é obrigatória"));
            }
            
            if (editarRequest.getFornecedor() == null || editarRequest.getFornecedor().trim().isEmpty()) {
                response.status(400);
                return toJson(ApiResponse.error("Fornecedor é obrigatório"));
            }
            
            Produto produtoAtualizado = ProdutoApiMapper.toDomainFromEditRequest(codigoBarras, editarRequest);
            Produto produtoEditado = editarProdutoUseCase.executar(codigoBarras, produtoAtualizado);
            
            ProdutoResponse produtoResponse = ProdutoApiMapper.toResponse(produtoEditado);

            response.status(200);
            return toJson(ApiResponse.success("Produto editado com sucesso", produtoResponse));

        } catch (Exception e) {
            response.status(500);
            return toJson(ApiResponse.error("Erro ao editar produto: " + e.getMessage()));
        }
    }

    public Object deletarProduto(spark.Request request, spark.Response response) {
        try {
            String codigoBarras = request.params(":codigoBarras");
            if (codigoBarras == null || codigoBarras.trim().isEmpty()) {
                response.status(400);
                return toJson(ApiResponse.error("Código de barras é obrigatório"));
            }
            
            deletarProdutoUseCase.executar(codigoBarras);

            response.status(200);
            return toJson(ApiResponse.success("Produto deletado com sucesso", null));

        } catch (Exception e) {
            response.status(500);
            return toJson(ApiResponse.error("Erro ao deletar produto: " + e.getMessage()));
        }
    }
    
    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            return "{\"success\":false,\"message\":\"Erro ao serializar JSON\"}";
        }
    }
}