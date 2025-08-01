package com.lucasmks.domain.usecase;

import com.lucasmks.domain.exception.ProdutoNaoEncontradoException;
import com.lucasmks.domain.exception.ValidacaoProdutoException;
import com.lucasmks.domain.model.Produto;
import com.lucasmks.domain.repository.ProdutoRepository;

public class EditarProdutoUseCase {
    private final ProdutoRepository repository;

    public EditarProdutoUseCase(ProdutoRepository repository) {
        this.repository = repository;
    }

    public Produto executar(String codigoBarras, Produto produtoAtualizado) {
        // 1. Validação de Parâmetros
        if (codigoBarras == null || codigoBarras.trim().isEmpty()) {
            throw new IllegalArgumentException("Código de barras não pode ser nulo ou vazio.");
        }
        
        if (produtoAtualizado == null) {
            throw new IllegalArgumentException("Produto atualizado não pode ser nulo.");
        }

        // 2. Verificar se o produto existe
        if (!repository.existePorCodigoBarras(codigoBarras)) {
            throw new ProdutoNaoEncontradoException("Produto com código de barras " + codigoBarras + " não encontrado.");
        }

        // 3. Validação de Dados do Produto
        validarDadosProduto(produtoAtualizado);

        // 4. Garantir que o código de barras não seja alterado
        produtoAtualizado.setCodigoBarras(codigoBarras);

        // 5. Atualizar o produto
        repository.atualizar(produtoAtualizado);
        
        // 6. Retornar o produto atualizado
        return repository.buscarPorCodigoBarras(codigoBarras)
                .orElseThrow(() -> new ProdutoNaoEncontradoException("Erro ao recuperar produto após atualização."));
    }

    private void validarDadosProduto(Produto produto) {
        if (produto.getCodigoBarras() == null || produto.getCodigoBarras().isBlank()) {
            throw new ValidacaoProdutoException("Código de barras não pode ser nulo ou vazio.");
        }
        if (produto.getNome() == null || produto.getNome().isBlank()) {
            throw new ValidacaoProdutoException("Nome do produto não pode ser nulo ou vazio.");
        }
        if (produto.getQuantidade() < 0) {
            throw new ValidacaoProdutoException("Quantidade não pode ser negativa.");
        }
        if (produto.getPrecoCusto() == null || produto.getPrecoCusto() < 0) {
            throw new ValidacaoProdutoException("Preço de custo não pode ser nulo ou negativo.");
        }
        if (produto.getPrecoVenda() == null || produto.getPrecoVenda() < 0) {
            throw new ValidacaoProdutoException("Preço de venda não pode ser nulo ou negativo.");
        }
        if (produto.getCategoria() == null || produto.getCategoria().isBlank()) {
            throw new ValidacaoProdutoException("Categoria não pode ser nula ou vazia.");
        }
        if (produto.getFornecedor() == null || produto.getFornecedor().isBlank()) {
            throw new ValidacaoProdutoException("Fornecedor não pode ser nulo ou vazio.");
        }
    }
} 