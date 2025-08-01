package com.lucasmks.domain.usecase;

import com.lucasmks.domain.exception.ProdutoJaExistenteException;
import com.lucasmks.domain.exception.ValidacaoProdutoException;
import com.lucasmks.domain.model.Produto;
import com.lucasmks.domain.repository.ProdutoRepository;

public class CadastrarProdutoUseCase {
    private final ProdutoRepository repository;

    public CadastrarProdutoUseCase(ProdutoRepository repository) {
        this.repository = repository;
    }

    public void executar(Produto produto) {
        // 1. Validação de Parâmetros
        if (produto == null) {
            throw new IllegalArgumentException("Produto não pode ser nulo para cadastro.");
        }

        // 2. Validação de Dados do Produto
        validarDadosProduto(produto);

        // 3. Verificação de Duplicidade
        if (repository.existePorCodigoBarras(produto.getCodigoBarras())) {
            throw new ProdutoJaExistenteException("Já existe um produto com o código de barras: " + produto.getCodigoBarras());
        }

        // 4. Salvar o novo produto
        repository.salvar(produto); 
    }

    // Método privado para agrupar as validações do produto
    private void validarDadosProduto(Produto produto) {
        if (produto.getCodigoBarras() == null || produto.getCodigoBarras().isBlank()) {
            throw new ValidacaoProdutoException("Código de barras não pode ser nulo ou vazio.");
        }
        if (produto.getNome() == null || produto.getNome().isBlank()) {
            throw new ValidacaoProdutoException("Nome do produto não pode ser nulo ou vazio.");
        }
        if (produto.getQuantidade() < 0) {
            throw new ValidacaoProdutoException("Quantidade inicial não pode ser negativa.");
        }
        if (produto.getPrecoCusto() == null || produto.getPrecoCusto() < 0) {
            throw new ValidacaoProdutoException("Preço de custo não pode ser nulo ou negativo.");
        }
        if (produto.getPrecoVenda() == null || produto.getPrecoVenda() < 0) {
            throw new ValidacaoProdutoException("Preço de venda não pode ser nulo ou negativo.");
        }
        // Poderíamos adicionar mais validações aqui, como categoria e fornecedor não vazios,
        // ou formato do código de barras, mas vamos focar nos testes existentes.
    }
}