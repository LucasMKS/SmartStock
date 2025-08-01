package com.lucasmks.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucasmks.domain.model.TipoMovimentacao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovimentacaoResponse {
    
    @JsonProperty("codigoBarrasProduto")
    private String codigoBarrasProduto;

    @JsonProperty("quantidade")
    private int quantidade;

    @JsonProperty("tipo")
    private TipoMovimentacao tipo;

    @JsonProperty("motivo")
    private String motivo;

    @JsonProperty("dataHora")
    private LocalDateTime dataHora;
}