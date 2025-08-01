package com.lucasmks.domain.usecase;

import java.util.Optional;

import com.lucasmks.domain.exception.ProdutoNaoEncontradoException;
import com.lucasmks.domain.model.Produto;
import com.lucasmks.domain.repository.ProdutoRepository;

public class BuscarProdutoUseCase {
    private final ProdutoRepository repository;

    public BuscarProdutoUseCase(ProdutoRepository repository) {
        this.repository = repository;
    }

    public Produto executar(String codigoBarras) {
        // 1. Validação de Parâmetros
        if (codigoBarras == null || codigoBarras.trim().isEmpty()) {
            throw new IllegalArgumentException("Código de barras não pode ser nulo ou vazio para busca.");
        }

        // 2. Verificação de Existência
        // Aqui, você pode usar 'existePorCodigoBarras' se implementou na interface ProdutoRepository
        if (!repository.existePorCodigoBarras(codigoBarras)) {
            throw new ProdutoNaoEncontradoException("Produto com código de barras " + codigoBarras + " não encontrado.");
        }

        // 3. Busca do Produto
        Optional<Produto> produtoOptional = repository.buscarPorCodigoBarras(codigoBarras);

        if (produtoOptional.isEmpty()) {
            throw new ProdutoNaoEncontradoException("Produto com código de barras " + codigoBarras + " não encontrado.");
        }

        // 4. Retorna o produto encontrado
        return produtoOptional.get();

    }

}
