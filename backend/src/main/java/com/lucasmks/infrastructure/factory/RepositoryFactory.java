package com.lucasmks.infrastructure.factory;

import com.lucasmks.domain.repository.ProdutoRepository;
import com.lucasmks.infrastructure.persistence.mapper.ProdutoMapper;
import com.lucasmks.infrastructure.persistence.repository.ProdutoMongoRepositoryImpl;

public class RepositoryFactory {
    
    private static ProdutoRepository produtoRepository;
    private static ProdutoMapper produtoMapper;
    
    public static ProdutoRepository getProdutoRepository() {
        if (produtoRepository == null) {
            produtoMapper = new ProdutoMapper();
            produtoRepository = new ProdutoMongoRepositoryImpl(produtoMapper);
        }
        return produtoRepository;
    }
}