package com.lucasmks.infrastructure.persistence.mapper;

import com.lucasmks.domain.model.Movimentacao;
import com.lucasmks.infrastructure.persistence.document.MovimentacaoDocument;

public class MovimentacaoMapper {
    public MovimentacaoDocument toDocument(Movimentacao movimentacao) {
        // Passa null para o ID, deixando o MongoDB gerá-lo automaticamente
        return new MovimentacaoDocument(
            movimentacao.getCodigoBarras(),
            movimentacao.getQuantidade(),
            movimentacao.getTipo(),
            movimentacao.getMotivo(),
            movimentacao.getDataHora()
        );
    }

    public Movimentacao toDomain(MovimentacaoDocument doc) {
        // Não precisa lidar com o ID aqui, pois a entidade de domínio não o tem.
        return new Movimentacao(
            doc.getCodigoBarras(),
            doc.getQuantidade(),
            doc.getTipo(),
            doc.getMotivo(),
            doc.getDataHora()
        );
    }
}