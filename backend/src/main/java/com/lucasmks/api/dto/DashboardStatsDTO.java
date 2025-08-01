package com.lucasmks.api.dto;

import java.util.Map;

public class DashboardStatsDTO {
    private long totalProdutos;
    private long estoqueBaixo;
    private double valorEstoqueCusto;
    private Map<String, Long> ultimas24h;

    public DashboardStatsDTO(long totalProdutos, long estoqueBaixo, double valorEstoqueCusto, Map<String, Long> ultimas24h) {
        this.totalProdutos = totalProdutos;
        this.estoqueBaixo = estoqueBaixo;
        this.valorEstoqueCusto = valorEstoqueCusto;
        this.ultimas24h = ultimas24h;
    }

    // Getters e Setters
    public long getTotalProdutos() { return totalProdutos; }
    public void setTotalProdutos(long totalProdutos) { this.totalProdutos = totalProdutos; }
    public long getEstoqueBaixo() { return estoqueBaixo; }
    public void setEstoqueBaixo(long estoqueBaixo) { this.estoqueBaixo = estoqueBaixo; }
    public double getValorEstoqueCusto() { return valorEstoqueCusto; }
    public void setValorEstoqueCusto(double valorEstoqueCusto) { this.valorEstoqueCusto = valorEstoqueCusto; }
    public Map<String, Long> getUltimas24h() { return ultimas24h; }
    public void setUltimas24h(Map<String, Long> ultimas24h) { this.ultimas24h = ultimas24h; }
}