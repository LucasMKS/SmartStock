package com.lucasmks.domain.usecase;

import com.lucasmks.domain.exception.ProdutoNaoEncontradoException;
import com.lucasmks.domain.exception.ValidacaoProdutoException;
import com.lucasmks.domain.model.Produto;
import com.lucasmks.domain.model.TipoMovimentacao;
import com.lucasmks.domain.model.Movimentacao;
import com.lucasmks.domain.repository.MovimentacaoRepository;
import com.lucasmks.domain.repository.ProdutoRepository;

public class AdicionarEstoqueUseCase {
    private final ProdutoRepository produtoRepository;
    private final MovimentacaoRepository movimentacaoRepository;

    public AdicionarEstoqueUseCase(ProdutoRepository produtoRepository, MovimentacaoRepository movimentacaoRepository) {
        this.produtoRepository = produtoRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    public void executar(String codigoBarras, int quantidadeAdicionar, String motivo) { // Adicionado 'motivo' para o histórico
        if (codigoBarras == null || codigoBarras.isBlank()) {
            throw new IllegalArgumentException("Código de barras não pode ser nulo ou vazio.");
        }
        if (quantidadeAdicionar <= 0) { // Alterado para <= 0, pois 0 também não deve adicionar
            throw new ValidacaoProdutoException("Quantidade a adicionar deve ser maior que 0.");
        }
        if (motivo == null || motivo.isBlank()) { // Validação do motivo
            throw new ValidacaoProdutoException("Motivo da entrada não pode ser nulo ou vazio.");
        }

        Produto produto = produtoRepository.buscarPorCodigoBarras(codigoBarras)
                .orElseThrow(() -> new ProdutoNaoEncontradoException("Produto não encontrado com código: " + codigoBarras));

        produto.setQuantidade(produto.getQuantidade() + quantidadeAdicionar);
        produtoRepository.salvar(produto); // Salva o produto atualizado

        // Lógica para registrar movimentação, se já estiver implementando Movimentacao
        movimentacaoRepository.salvar(new Movimentacao(codigoBarras, quantidadeAdicionar, TipoMovimentacao.ENTRADA, motivo));
    }
    

}
