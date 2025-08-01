package com.lucasmks.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditarProdutoRequest {
    private String nome;
    private String categoria;
    private int quantidade;
    private Double precoCusto;
    private Double precoVenda;
    private String fornecedor;
} 