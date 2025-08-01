package com.lucasmks.api.service;

import com.lucasmks.api.dto.DashboardStatsDTO;
import com.lucasmks.domain.model.Movimentacao;
import com.lucasmks.domain.model.TipoMovimentacao;
import com.lucasmks.infrastructure.database.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DashboardService {

    private final MongoCollection<Document> produtoCollection;
    private final MongoCollection<Movimentacao> movimentacaoCollection;
    private static final int LIMITE_ESTOQUE_BAIXO = 10;

    public DashboardService() {
        this.produtoCollection = MongoConnection.getDatabase().getCollection("produtos");
        this.movimentacaoCollection = MongoConnection.getDatabase().getCollection("movimentacoes", Movimentacao.class);
    }

    public DashboardStatsDTO getDashboardStats() {
        long totalProdutos = produtoCollection.countDocuments();
        long estoqueBaixo = produtoCollection.countDocuments(Filters.lte("quantidade", LIMITE_ESTOQUE_BAIXO));
        double valorEstoqueCusto = calcularValorTotalEstoque();
        Map<String, Long> ultimas24h = getMovimentacoesUltimas24h();

        return new DashboardStatsDTO(totalProdutos, estoqueBaixo, valorEstoqueCusto, ultimas24h);
    }

    private double calcularValorTotalEstoque() {
        Bson groupStage = Aggregates.group(null,
                Accumulators.sum("totalValue", new Document("$multiply", Arrays.asList("$quantidade", "$precoCusto"))));

        Document result = produtoCollection.aggregate(Arrays.asList(groupStage)).first();
        return result != null ? result.getDouble("totalValue") : 0.0;
    }

    private Map<String, Long> getMovimentacoesUltimas24h() {
        LocalDateTime vinteQuatroHorasAtras = LocalDateTime.now().minusHours(24);
        Bson filter = Filters.gte("dataHora", vinteQuatroHorasAtras);

        long entradas = movimentacaoCollection.countDocuments(Filters.and(filter, Filters.eq("tipo", TipoMovimentacao.ENTRADA.name())));
        long saidas = movimentacaoCollection.countDocuments(Filters.and(filter, Filters.eq("tipo", TipoMovimentacao.SAIDA.name())));

        Map<String, Long> movimentacoes = new HashMap<>();
        movimentacoes.put("entradas", entradas);
        movimentacoes.put("saidas", saidas);

        return movimentacoes;
    }
}