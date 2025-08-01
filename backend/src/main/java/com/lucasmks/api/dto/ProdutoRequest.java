package com.lucasmks.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoRequest {
    
    @JsonProperty("codigoBarras")
    private String codigoBarras;
    
    @JsonProperty("nome")
    private String nome;
    
    @JsonProperty("categoria")
    private String categoria;
    
    @JsonProperty("quantidade")
    private int quantidade;
    
    @JsonProperty("precoCusto")
    private Double precoCusto;
    
    @JsonProperty("precoVenda")
    private Double precoVenda;
    
    @JsonProperty("fornecedor")
    private String fornecedor;

}