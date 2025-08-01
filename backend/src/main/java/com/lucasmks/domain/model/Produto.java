package com.lucasmks.domain.model;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Produto {
    private String codigoBarras;
    private String nome;
    private String categoria;
    private int quantidade;
    private Double precoCusto;
    private Double precoVenda;
    private String fornecedor;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Produto produto = (Produto) o;
        return Objects.equals(codigoBarras, produto.codigoBarras);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigoBarras);
    }
} 