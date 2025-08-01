package com.lucasmks.infrastructure.persistence.mapper;

import com.lucasmks.domain.model.Produto;
import com.lucasmks.infrastructure.persistence.document.ProdutoDocument;

public class ProdutoMapper {
    
    public ProdutoDocument toDocument(Produto produto) {
        if (produto == null) {
            return null;
        }
        
        return new ProdutoDocument(
            produto.getCodigoBarras(),
            produto.getNome(),
            produto.getCategoria(),
            produto.getQuantidade(),
            produto.getPrecoCusto(),
            produto.getPrecoVenda(),
            produto.getFornecedor()
        );
    }
    
    public Produto toDomain(ProdutoDocument document) {
        if (document == null) {
            return null;
        }
        
        return new Produto(
            document.getCodigoBarras(),
            document.getNome(),
            document.getCategoria(),
            document.getQuantidade(),
            document.getPrecoCusto(),
            document.getPrecoVenda(),
            document.getFornecedor()
        );
    }
}