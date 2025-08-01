package com.lucasmks.api.mapper;

import com.lucasmks.api.dto.MovimentacaoResponse;
import com.lucasmks.domain.model.Movimentacao;

import java.util.List;
import java.util.stream.Collectors;

public class MovimentacaoApiMapper {

    public static MovimentacaoResponse toResponse(Movimentacao movimentacao) {
        return new MovimentacaoResponse(
            movimentacao.getCodigoBarras(), // No DTO use "codigoBarrasProduto" se for mais claro
            movimentacao.getQuantidade(),
            movimentacao.getTipo(),
            movimentacao.getMotivo(),
            movimentacao.getDataHora()
        );
    }

    public static List<MovimentacaoResponse> toResponseList(List<Movimentacao> movimentacoes) {
        return movimentacoes.stream()
                .map(MovimentacaoApiMapper::toResponse)
                .collect(Collectors.toList());
    }
}