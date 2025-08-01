package com.lucasmks.infrastructure.persistence.repository;

import com.lucasmks.domain.model.Movimentacao;
import com.lucasmks.domain.model.TipoMovimentacao;
import com.lucasmks.domain.repository.MovimentacaoRepository;
import com.lucasmks.infrastructure.database.MongoConnection;
import com.lucasmks.infrastructure.persistence.document.MovimentacaoDocument;
import com.lucasmks.infrastructure.persistence.mapper.MovimentacaoMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.*;

public class MovimentacaoMongoRepositoryImpl implements MovimentacaoRepository {
    private final MongoCollection<MovimentacaoDocument> collection;
    private final MovimentacaoMapper mapper;

    public MovimentacaoMongoRepositoryImpl(MovimentacaoMapper mapper) {
        MongoDatabase db = MongoConnection.getDatabase();
        this.collection = db.getCollection("movimentacoes", MovimentacaoDocument.class);
        this.mapper = mapper;
    }

    @Override
    public void salvar(Movimentacao movimentacao) {
        MovimentacaoDocument doc = mapper.toDocument(movimentacao);
        collection.insertOne(doc); 
    }

    @Override
    public List<Movimentacao> buscarTodasMovimentacoes() {
        List<Movimentacao> result = new ArrayList<>();
        for (MovimentacaoDocument doc : collection.find()) {
            result.add(mapper.toDomain(doc));
        }
        return result;
    }

    @Override
    public List<Movimentacao> buscarMovimentacoesPorProduto(String codigoBarras) {
        List<Movimentacao> result = new ArrayList<>();
        // O nome do campo na coleção é 'codigoBarras' (do MovimentacaoDocument)
        for (MovimentacaoDocument doc : collection.find(eq("codigoBarras", codigoBarras))) {
            result.add(mapper.toDomain(doc));
        }
        return result;
    }

    @Override
    public List<Movimentacao> buscarMovimentacoesPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        List<Movimentacao> result = new ArrayList<>();
        // O nome do campo na coleção é 'dataHora' (do MovimentacaoDocument)
        for (MovimentacaoDocument doc : collection.find(and(gte("dataHora", dataInicio), lte("dataHora", dataFim)))) {
            result.add(mapper.toDomain(doc));
        }
        return result;
    }

    @Override
    public List<Movimentacao> buscarMovimentacoesPorMotivo(String motivo) {
        List<Movimentacao> result = new ArrayList<>();
        for (MovimentacaoDocument doc : collection.find(eq("motivo", motivo))) {
            result.add(mapper.toDomain(doc));
        }
        return result;
    }

    @Override
    public List<Movimentacao> buscarMovimentacoesPorTipo(TipoMovimentacao tipo) {
        List<Movimentacao> result = new ArrayList<>();
        // Salvar enum como String, então buscar por String
        for (MovimentacaoDocument doc : collection.find(eq("tipo", tipo.name()))) {
            result.add(mapper.toDomain(doc));
        }
        return result;
    }

    @Override
    public Optional<Movimentacao> getUltimaMovimentacao() {
        MovimentacaoDocument doc = collection.find().sort(new Document("dataHora", -1)).first();
        if (doc == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(mapper.toDomain(doc));
    }
} 