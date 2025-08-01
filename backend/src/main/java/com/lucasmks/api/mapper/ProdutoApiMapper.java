package com.lucasmks.api.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.lucasmks.api.dto.EditarProdutoRequest;
import com.lucasmks.api.dto.ProdutoRequest;
import com.lucasmks.api.dto.ProdutoResponse;
import com.lucasmks.domain.model.Produto;

public class ProdutoApiMapper {
    
    public static Produto toDomain(ProdutoRequest request) {
        if (request == null) {
            return null;
        }
        
        return new Produto(
            request.getCodigoBarras(),
            request.getNome(),
            request.getCategoria(),
            request.getQuantidade(),
            request.getPrecoCusto(),
            request.getPrecoVenda(),
            request.getFornecedor()
        );
    }
    
    public static ProdutoResponse toResponse(Produto produto) {
        if (produto == null) {
            return null;
        }
        
        return new ProdutoResponse(
            produto.getCodigoBarras(),
            produto.getNome(),
            produto.getCategoria(),
            produto.getQuantidade(),
            produto.getPrecoCusto(),
            produto.getPrecoVenda(),
            produto.getFornecedor()
        );
    }

    public static List<ProdutoResponse> toResponseList(List<Produto> produtos) {
        if (produtos == null) { // Adicione null check
            return List.of(); // Retorna lista vazia se nulo
        }
        return produtos.stream()
                .map(ProdutoApiMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    public static Produto toDomainFromEditRequest(String codigoBarras, EditarProdutoRequest request) {
        if (request == null) {
            return null;
        }
        
        return new Produto(
            codigoBarras,
            request.getNome(),
            request.getCategoria(),
            request.getQuantidade(),
            request.getPrecoCusto(),
            request.getPrecoVenda(),
            request.getFornecedor()
        );
    }
}