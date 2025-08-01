package com.lucasmks.infrastructure.repository.fake;

import com.lucasmks.domain.model.Movimentacao;
import com.lucasmks.domain.model.TipoMovimentacao;
import com.lucasmks.domain.repository.MovimentacaoRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional; // Importe Optional
import java.util.stream.Collectors;

public class MovimentacaoRepositoryFake implements MovimentacaoRepository {

    private final List<Movimentacao> movimentacoes = new ArrayList<>();

    @Override
    public void salvar(Movimentacao movimentacao) {
        movimentacoes.add(movimentacao);
    }

    @Override
    public List<Movimentacao> buscarTodasMovimentacoes() {
        return new ArrayList<>(movimentacoes);
    }

    @Override
    public List<Movimentacao> buscarMovimentacoesPorProduto(String codigoBarrasProduto) {
        return movimentacoes.stream()
                .filter(m -> m.getCodigoBarras().equals(codigoBarrasProduto))
                .collect(Collectors.toList());
    }

    @Override
    public List<Movimentacao> buscarMovimentacoesPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        return movimentacoes.stream()
                .filter(m -> !m.getDataHora().isBefore(dataInicio) && !m.getDataHora().isAfter(dataFim))
                .collect(Collectors.toList());
    }

    @Override
    public List<Movimentacao> buscarMovimentacoesPorMotivo(String motivo) {
        return movimentacoes.stream()
                .filter(m -> m.getMotivo() != null && m.getMotivo().equalsIgnoreCase(motivo))
                .collect(Collectors.toList());
    }

    @Override
    public List<Movimentacao> buscarMovimentacoesPorTipo(TipoMovimentacao tipo) {
        return movimentacoes.stream()
                .filter(m -> m.getTipo() == tipo)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Movimentacao> getUltimaMovimentacao() { // <-- Nova implementação
        if (movimentacoes.isEmpty()) {
            return Optional.empty();
        }
        // Retorna o último elemento da lista
        return Optional.of(movimentacoes.get(movimentacoes.size() - 1));
    }

    public void limpar() {
        movimentacoes.clear();
    }
}