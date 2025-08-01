package com.lucasmks.domain.usecase;

import java.time.LocalDateTime;
import java.util.List;

import com.lucasmks.domain.model.Movimentacao;
import com.lucasmks.domain.model.TipoMovimentacao;
import com.lucasmks.domain.repository.MovimentacaoRepository;
import com.lucasmks.domain.repository.ProdutoRepository;

public class ConsultarHistoricoMovimentacaoUseCase {
    private final ProdutoRepository produtoRepository;
    private final MovimentacaoRepository movimentacaoRepository;

    public ConsultarHistoricoMovimentacaoUseCase(ProdutoRepository produtoRepository, MovimentacaoRepository movimentacaoRepository) {
        this.produtoRepository = produtoRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    // Método para obter historico de movimentações
    public List<Movimentacao> buscarTodasMovimentacoes() {
        if (movimentacaoRepository.buscarTodasMovimentacoes() == null || movimentacaoRepository.buscarTodasMovimentacoes().isEmpty()) {
            throw new IllegalStateException("Nenhuma movimentação registrada.");
        }

        // Retorna todas as movimentações
        return movimentacaoRepository.buscarTodasMovimentacoes();
    }

    // Método para consultar movimentações por código de barras
    public List<Movimentacao> buscarMovimentacoesPorProduto(String codigoBarras) {
        if (codigoBarras == null || codigoBarras.isBlank()) {
            throw new IllegalArgumentException("Código de barras não pode ser nulo ou vazio.");
        }
        // Validar se o produto existe
        if (!produtoRepository.existePorCodigoBarras(codigoBarras)) {
            throw new IllegalArgumentException("Produto com código de barras " + codigoBarras + " não encontrado.");
        }

        // Retornar movimentações filtradas pelo código de barras
        return movimentacaoRepository.buscarMovimentacoesPorProduto(codigoBarras);
    }
    
    public List<Movimentacao> buscarMovimentacoesPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        if (dataInicio == null || dataFim == null) {
            throw new IllegalArgumentException("As datas de início e fim não podem ser nulas.");
        }
        if (dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("A data de início não pode ser posterior à data de fim.");
        }
        return movimentacaoRepository.buscarMovimentacoesPorPeriodo(dataInicio, dataFim);
    }

    // Adicionando os outros métodos de busca que seus testes de histórico esperam
    public List<Movimentacao> buscarMovimentacoesPorMotivo(String motivo) {
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("O motivo não pode ser nulo ou vazio.");
        }
        return movimentacaoRepository.buscarMovimentacoesPorMotivo(motivo);
    }

    public List<Movimentacao> buscarMovimentacoesPorTipo(TipoMovimentacao tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("O tipo de movimentação não pode ser nulo.");
        }
        return movimentacaoRepository.buscarMovimentacoesPorTipo(tipo);
    }
}
