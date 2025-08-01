package com.lucasmks.infrastructure.repository.fake;
 // simular o armazenamento de produtos em memória, usando um HashMap

import com.lucasmks.domain.model.Produto;
import com.lucasmks.domain.repository.ProdutoRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ProdutoRepositoryFake implements ProdutoRepository {

    // Simula um banco de dados em memória
    private final Map<String, Produto> produtos = new HashMap<>();

    @Override
    public void salvar(Produto produto) {
        // No repositório fake, simplesmente adicionamos ou atualizamos o produto.
        // As validações de duplicidade serão feitas no UseCase.
        produtos.put(produto.getCodigoBarras(), produto);
    }

    @Override
    public Optional<Produto> buscarPorCodigoBarras(String codigoBarras) {
        return Optional.ofNullable(produtos.get(codigoBarras));
    }

    // Adicionei um método extra para simular se um produto já existe,
    // o que é útil para o caso de uso de cadastro.
    @Override
    public boolean existePorCodigoBarras(String codigoBarras) {
        return produtos.containsKey(codigoBarras);
    }

    @Override
    public void remover(String codigoBarras) {
        produtos.remove(codigoBarras);
    }

    @Override
    public void atualizar(Produto produto) {
        produtos.put(produto.getCodigoBarras(), produto);
    }

    @Override
    public List<Produto> buscarTodos() {
        return new ArrayList<>(produtos.values());
    }

    // Método para limpar o "banco de dados" entre os testes, se necessário.
    public void limpar() {
        produtos.clear();
    }
}