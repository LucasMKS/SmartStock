package com.lucasmks.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstoqueRequest {
    @JsonProperty("quantidade")
    private int quantidade;

    @JsonProperty("motivo")
    private String motivo;
    
    // O código de barras virá da URL, não do corpo da requisição
}