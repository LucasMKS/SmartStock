package com.lucasmks.infrastructure.persistence.document;

import org.bson.Document;
import org.bson.types.ObjectId;

public class ProdutoDocument {
    
    private ObjectId id;
    private String codigoBarras;
    private String nome;
    private String categoria;
    private int quantidade;
    private Double precoCusto;
    private Double precoVenda;
    private String fornecedor;

    // Construtores
    public ProdutoDocument() {}
    
    public ProdutoDocument(String codigoBarras, String nome, String categoria, 
                          int quantidade, Double precoCusto, 
                          Double precoVenda, String fornecedor) {
        this.codigoBarras = codigoBarras;
        this.nome = nome;
        this.categoria = categoria;
        this.quantidade = quantidade;
        this.precoCusto = precoCusto;
        this.precoVenda = precoVenda;
        this.fornecedor = fornecedor;
    }

    // Conversão para Document BSON
    public Document toDocument() {
        Document doc = new Document();
        if (id != null) {
            doc.append("_id", id);
        }
        doc.append("codigoBarras", codigoBarras)
           .append("nome", nome)
           .append("categoria", categoria)
           .append("quantidade", quantidade)
           .append("precoCusto", precoCusto)
           .append("precoVenda", precoVenda)
           .append("fornecedor", fornecedor);
        return doc;
    }

    // Conversão de Document BSON
    public static ProdutoDocument fromDocument(Document doc) {
        if (doc == null) {
            return null;
        }
        
        ProdutoDocument produto = new ProdutoDocument();
        produto.id = doc.getObjectId("_id");
        produto.codigoBarras = doc.getString("codigoBarras");
        produto.nome = doc.getString("nome");
        produto.categoria = doc.getString("categoria");
        produto.quantidade = doc.getInteger("quantidade", 0); 
        
        Number precoCustoNum = doc.get("precoCusto", Number.class);
        produto.precoCusto = precoCustoNum != null ? precoCustoNum.doubleValue() : null;

        Number precoVendaNum = doc.get("precoVenda", Number.class);
        produto.precoVenda = precoVendaNum != null ? precoVendaNum.doubleValue() : null;

        produto.fornecedor = doc.getString("fornecedor");
        return produto;
    }

    // Getters e Setters
    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }
    
    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    
    public Double getPrecoCusto() { return precoCusto; }
    public void setPrecoCusto(Double precoCusto) { this.precoCusto = precoCusto; }
    
    public Double getPrecoVenda() { return precoVenda; }
    public void setPrecoVenda(Double precoVenda) { this.precoVenda = precoVenda; }
    
    public String getFornecedor() { return fornecedor; }
    public void setFornecedor(String fornecedor) { this.fornecedor = fornecedor; }
}