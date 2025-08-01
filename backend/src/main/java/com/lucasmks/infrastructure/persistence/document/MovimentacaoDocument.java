package com.lucasmks.infrastructure.persistence.document;

import com.lucasmks.domain.model.TipoMovimentacao;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.codecs.pojo.annotations.BsonId;
import java.time.LocalDateTime;
import org.bson.Document;
import org.bson.types.ObjectId;

public class MovimentacaoDocument {
    @BsonId
    private ObjectId id;

    @BsonProperty("codigoBarras")
    private String codigoBarras;

    @BsonProperty("quantidade")
    private int quantidade;

    @BsonProperty("tipo")
    private TipoMovimentacao tipo;

    @BsonProperty("motivo")
    private String motivo;

    @BsonProperty("dataHora")
    private LocalDateTime dataHora;

    public MovimentacaoDocument() {}

    // Construtor para criar a partir do domínio (sem ID, MongoDB gera)
    public MovimentacaoDocument(String codigoBarras, int quantidade, TipoMovimentacao tipo, String motivo, LocalDateTime dataHora) {
        this.codigoBarras = codigoBarras;
        this.quantidade = quantidade;
        this.tipo = tipo;
        this.motivo = motivo;
        this.dataHora = dataHora;
    }

    // Construtor completo (se precisar criar MovimentacaoDocument com ID específico)
    public MovimentacaoDocument(ObjectId id, String codigoBarras, int quantidade, TipoMovimentacao tipo, String motivo, LocalDateTime dataHora) {
        this.id = id;
        this.codigoBarras = codigoBarras;
        this.quantidade = quantidade;
        this.tipo = tipo;
        this.motivo = motivo;
        this.dataHora = dataHora;
    }

    // --- Métodos de Conversão para/de Document BSON (para seguir o padrão ProdutoDocument) ---
    public Document toDocument() {
        Document doc = new Document();
        if (id != null) {
            doc.append("_id", id);
        }
        doc.append("codigoBarras", codigoBarras)
           .append("quantidade", quantidade)
           .append("tipo", tipo != null ? tipo.name() : null)
           .append("motivo", motivo)
           .append("dataHora", dataHora);
        return doc;
    }

    public static MovimentacaoDocument fromDocument(Document doc) {
        if (doc == null) {
            return null;
        }
        MovimentacaoDocument movimentacao = new MovimentacaoDocument();
        movimentacao.id = doc.getObjectId("_id");
        movimentacao.codigoBarras = doc.getString("codigoBarras");
        movimentacao.quantidade = doc.getInteger("quantidade", 0);
        String tipoStr = doc.getString("tipo");
        movimentacao.tipo = tipoStr != null ? TipoMovimentacao.valueOf(tipoStr) : null;
        movimentacao.dataHora = doc.get("dataHora", LocalDateTime.class);
        movimentacao.motivo = doc.getString("motivo");
        return movimentacao;
    }

    // --- Getters e Setters (ajustados para o novo nome do campo) ---
    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }
    
    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }
    
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    
    public TipoMovimentacao getTipo() { return tipo; }
    public void setTipo(TipoMovimentacao tipo) { this.tipo = tipo; }
    
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}