package com.lucasmks.api.service;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasmks.api.dto.ApiResponse;
import com.lucasmks.api.dto.EditarProdutoRequest;
import com.lucasmks.api.dto.EstoqueRequest;
import com.lucasmks.api.dto.ProdutoRequest;
import com.lucasmks.api.dto.ProdutoResponse;
import com.lucasmks.api.mapper.ProdutoApiMapper;
import com.lucasmks.domain.model.Produto;
import com.lucasmks.domain.usecase.AdicionarEstoqueUseCase;
import com.lucasmks.domain.usecase.BuscarProdutoUseCase;
import com.lucasmks.domain.usecase.CadastrarProdutoUseCase;
import com.lucasmks.domain.usecase.DeletarProdutoUseCase;
import com.lucasmks.domain.usecase.EditarProdutoUseCase;
import com.lucasmks.domain.usecase.ListarTodosProdutosUseCase;
import com.lucasmks.domain.usecase.RemoverEstoqueUseCase;

public class ProdutoService {
    
    private final ObjectMapper objectMapper;
    private final CadastrarProdutoUseCase cadastrarProdutoUseCase;
    private final BuscarProdutoUseCase buscarProdutoUseCase;
    private final AdicionarEstoqueUseCase adicionarEstoqueUseCase;
    private final RemoverEstoqueUseCase removerEstoqueUseCase;
    private final ListarTodosProdutosUseCase listarTodosProdutosUseCase;
    private final EditarProdutoUseCase editarProdutoUseCase;
    private final DeletarProdutoUseCase deletarProdutoUseCase;
    
    public ProdutoService(
            CadastrarProdutoUseCase cadastrarProdutoUseCase,
            BuscarProdutoUseCase buscarProdutoUseCase,
            AdicionarEstoqueUseCase adicionarEstoqueUseCase,
            RemoverEstoqueUseCase removerEstoqueUseCase,
            ListarTodosProdutosUseCase listarTodosProdutosUseCase,
            EditarProdutoUseCase editarProdutoUseCase,
            DeletarProdutoUseCase deletarProdutoUseCase) {
        
        this.objectMapper = new ObjectMapper();
        this.cadastrarProdutoUseCase = cadastrarProdutoUseCase;
        this.buscarProdutoUseCase = buscarProdutoUseCase;
        this.adicionarEstoqueUseCase = adicionarEstoqueUseCase;
        this.removerEstoqueUseCase = removerEstoqueUseCase;
        this.listarTodosProdutosUseCase = listarTodosProdutosUseCase;
        this.editarProdutoUseCase = editarProdutoUseCase;
        this.deletarProdutoUseCase = deletarProdutoUseCase;
    }

    public Object listarProdutos(spark.Request request, spark.Response response) {
        try {
            List<Produto> produtos = listarTodosProdutosUseCase.executar();
            List<ProdutoResponse> produtosResponse = ProdutoApiMapper.toResponseList(produtos);
           return toJson(ApiResponse.success("Lista de produtos carregada com sucesso", produtosResponse));
        } catch (Exception e) {
            response.status(500);
            return toJson(ApiResponse.error("Erro ao listar produtos: " + e.getMessage()));
        }
    }
    
    public ApiResponse<ProdutoResponse> buscarProduto(String codigoBarras) {
        try {
            if (codigoBarras == null || codigoBarras.trim().isEmpty()) {
                return ApiResponse.error("Código de barras é obrigatório");
            }
            
            Produto produto = buscarProdutoUseCase.executar(codigoBarras);
            
            if (produto != null) {
                ProdutoResponse produtoResponse = ProdutoApiMapper.toResponse(produto);
                return ApiResponse.success("Produto encontrado", produtoResponse);
            } else {
                return ApiResponse.error("Produto não encontrado");
            }
            
        } catch (Exception e) {
            return ApiResponse.error("Erro ao buscar produto: " + e.getMessage());
        }
    }
    
    public ApiResponse<ProdutoResponse> cadastrarProduto(String body) {
        try {
            if (body == null || body.trim().isEmpty()) {
                return ApiResponse.error("Corpo da requisição é obrigatório");
            }
            
            ProdutoRequest produtoRequest = objectMapper.readValue(body, ProdutoRequest.class);
            
            // Validações básicas
            if (produtoRequest.getCodigoBarras() == null || produtoRequest.getCodigoBarras().trim().isEmpty()) {
                return ApiResponse.error("Código de barras é obrigatório");
            }
            
            if (produtoRequest.getNome() == null || produtoRequest.getNome().trim().isEmpty()) {
                return ApiResponse.error("Nome é obrigatório");
            }
            
            Produto produto = ProdutoApiMapper.toDomain(produtoRequest);
            cadastrarProdutoUseCase.executar(produto);
            
            ProdutoResponse produtoResponse = ProdutoApiMapper.toResponse(produto);
            return ApiResponse.success("Produto cadastrado com sucesso", produtoResponse);
            
        } catch (Exception e) {
            return ApiResponse.error("Erro ao cadastrar produto: " + e.getMessage());
        }
    }

    public ApiResponse<Void> adicionarEstoque(String codigoBarras, String body) {
        try {
            EstoqueRequest estoqueRequest = objectMapper.readValue(body, EstoqueRequest.class);

            // Validações básicas de input HTTP
            if (codigoBarras == null || codigoBarras.trim().isEmpty()) {
                return ApiResponse.error("Código de barras é obrigatório.");
            }
            if (estoqueRequest.getQuantidade() <= 0) {
                return ApiResponse.error("Quantidade a adicionar deve ser maior que zero.");
            }
            if (estoqueRequest.getMotivo() == null || estoqueRequest.getMotivo().trim().isEmpty()) {
                return ApiResponse.error("Motivo da adição de estoque é obrigatório.");
            }

            adicionarEstoqueUseCase.executar(codigoBarras, estoqueRequest.getQuantidade(), estoqueRequest.getMotivo());

            return ApiResponse.success("Estoque adicionado com sucesso para o produto " + codigoBarras, null);

        } catch (Exception e) {
            return ApiResponse.error("Erro ao adicionar estoque: " + e.getMessage());
        }
    }

    public ApiResponse<Void> removerEstoque(String codigoBarras, String body) {
        try {
            EstoqueRequest estoqueRequest = objectMapper.readValue(body, EstoqueRequest.class);

            // Validações básicas de input HTTP
            if (codigoBarras == null || codigoBarras.trim().isEmpty()) {
                return ApiResponse.error("Código de barras é obrigatório.");
            }
            if (estoqueRequest.getQuantidade() <= 0) {
                return ApiResponse.error("Quantidade a remover deve ser maior que zero.");
            }
            if (estoqueRequest.getMotivo() == null || estoqueRequest.getMotivo().trim().isEmpty()) {
                return ApiResponse.error("Motivo da remoção de estoque é obrigatório.");
            }

            removerEstoqueUseCase.executar(codigoBarras, estoqueRequest.getQuantidade(), estoqueRequest.getMotivo());

            return ApiResponse.success("Estoque removido com sucesso para o produto " + codigoBarras, null);

        } catch (Exception e) {
            return ApiResponse.error("Erro ao remover estoque: " + e.getMessage());
        }
    }

    public ApiResponse<ProdutoResponse> editarProduto(String codigoBarras, String body) {
        try {
            if (codigoBarras == null || codigoBarras.trim().isEmpty()) {
                return ApiResponse.error("Código de barras é obrigatório");
            }
            
            if (body == null || body.trim().isEmpty()) {
                return ApiResponse.error("Corpo da requisição é obrigatório");
            }
            
            EditarProdutoRequest editarRequest = objectMapper.readValue(body, EditarProdutoRequest.class);
            
            // Validações básicas
            if (editarRequest.getNome() == null || editarRequest.getNome().trim().isEmpty()) {
                return ApiResponse.error("Nome é obrigatório");
            }
            
            if (editarRequest.getCategoria() == null || editarRequest.getCategoria().trim().isEmpty()) {
                return ApiResponse.error("Categoria é obrigatória");
            }
            
            if (editarRequest.getFornecedor() == null || editarRequest.getFornecedor().trim().isEmpty()) {
                return ApiResponse.error("Fornecedor é obrigatório");
            }
            
            Produto produtoAtualizado = ProdutoApiMapper.toDomainFromEditRequest(codigoBarras, editarRequest);
            Produto produtoEditado = editarProdutoUseCase.executar(codigoBarras, produtoAtualizado);
            
            ProdutoResponse produtoResponse = ProdutoApiMapper.toResponse(produtoEditado);
            return ApiResponse.success("Produto editado com sucesso", produtoResponse);
            
        } catch (Exception e) {
            return ApiResponse.error("Erro ao editar produto: " + e.getMessage());
        }
    }

    public ApiResponse<Void> deletarProduto(String codigoBarras) {
        try {
            if (codigoBarras == null || codigoBarras.trim().isEmpty()) {
                return ApiResponse.error("Código de barras é obrigatório");
            }
            
            deletarProdutoUseCase.executar(codigoBarras);
            return ApiResponse.success("Produto deletado com sucesso", null);
            
        } catch (Exception e) {
            return ApiResponse.error("Erro ao deletar produto: " + e.getMessage());
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