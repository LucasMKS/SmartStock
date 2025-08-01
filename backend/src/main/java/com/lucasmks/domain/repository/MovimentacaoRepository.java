package com.lucasmks.domain.repository;

import com.lucasmks.domain.model.Movimentacao;
import com.lucasmks.domain.model.TipoMovimentacao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MovimentacaoRepository {
    void salvar(Movimentacao movimentacao);
    List<Movimentacao> buscarTodasMovimentacoes();
    List<Movimentacao> buscarMovimentacoesPorProduto(String codigoBarras);
    List<Movimentacao> buscarMovimentacoesPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim);
    List<Movimentacao> buscarMovimentacoesPorMotivo(String motivo);
    List<Movimentacao> buscarMovimentacoesPorTipo(TipoMovimentacao tipo);
    Optional<Movimentacao> getUltimaMovimentacao();
}