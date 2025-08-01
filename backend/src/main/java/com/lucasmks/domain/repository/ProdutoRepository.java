package com.lucasmks.domain.repository;

import java.util.List;
import java.util.Optional;

import com.lucasmks.domain.model.Produto;

public interface ProdutoRepository {
    void salvar(Produto produto);
    
    Optional<Produto> buscarPorCodigoBarras(String codigoBarras);

    boolean existePorCodigoBarras(String codigoBarras); // <-- Adicionado
    
    void remover(String codigoBarras);
    
    void atualizar(Produto produto);

    List<Produto> buscarTodos();
}