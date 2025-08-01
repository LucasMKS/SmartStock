package com.lucasmks.domain.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor; // Adicionado para facilitar a criação em testes/mocks

@Data
@AllArgsConstructor
@NoArgsConstructor // Adicionado para permitir construtores sem todos os args
public class Movimentacao {
    private String codigoBarras;
    private int quantidade;
    private TipoMovimentacao tipo;
    private String motivo;
    private LocalDateTime dataHora;

    // Construtor auxiliar para facilitar a criação nos UseCases
    public Movimentacao(String codigoBarras, int quantidade, TipoMovimentacao tipo, String motivo) {
        this.codigoBarras = codigoBarras;
        this.quantidade = quantidade;
        this.tipo = tipo;
        this.motivo = motivo;
        this.dataHora = LocalDateTime.now();
    }
}