package com.lucasmks.domain.usecase;

import com.lucasmks.domain.model.Produto;
import com.lucasmks.domain.repository.ProdutoRepository;
import java.util.List;

public class ListarTodosProdutosUseCase {
    private final ProdutoRepository produtoRepository;

    public ListarTodosProdutosUseCase(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public List<Produto> executar() {
        return produtoRepository.buscarTodos(); // Este método precisa ser adicionado ao ProdutoRepository
    }
}