package com.lucasmks.domain.usecase;

import com.lucasmks.domain.exception.ProdutoNaoEncontradoException;
import com.lucasmks.domain.repository.ProdutoRepository;

public class DeletarProdutoUseCase {
    private final ProdutoRepository repository;

    public DeletarProdutoUseCase(ProdutoRepository repository) {
        this.repository = repository;
    }

    public void executar(String codigoBarras) {
        // 1. Validação de Parâmetros
        if (codigoBarras == null || codigoBarras.trim().isEmpty()) {
            throw new IllegalArgumentException("Código de barras não pode ser nulo ou vazio.");
        }

        // 2. Verificar se o produto existe
        if (!repository.existePorCodigoBarras(codigoBarras)) {
            throw new ProdutoNaoEncontradoException("Produto com código de barras " + codigoBarras + " não encontrado.");
        }

        // 3. Remover o produto
        repository.remover(codigoBarras);
    }
} 