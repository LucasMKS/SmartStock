package com.lucasmks.infrastructure.persistence.repository;

import com.lucasmks.domain.model.Produto;  // Import correto: model
import com.lucasmks.domain.repository.ProdutoRepository;
import com.lucasmks.infrastructure.database.MongoConnection;
import com.lucasmks.infrastructure.persistence.document.ProdutoDocument;
import com.lucasmks.infrastructure.persistence.mapper.ProdutoMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProdutoMongoRepositoryImpl implements ProdutoRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(ProdutoMongoRepositoryImpl.class);
    private static final String COLLECTION_NAME = "produtos";
    
    private final MongoCollection<Document> collection;
    private final ProdutoMapper mapper;
    
    public ProdutoMongoRepositoryImpl(ProdutoMapper mapper) {
        this.mapper = mapper;
        try {
            logger.debug("Inicializando repositório MongoDB para produtos...");
            MongoDatabase database = MongoConnection.getDatabase();
            this.collection = database.getCollection(COLLECTION_NAME);
            logger.info("Repositório MongoDB inicializado com sucesso para coleção: {}", COLLECTION_NAME);
        } catch (Exception e) {
            logger.error("Erro ao inicializar repositório MongoDB: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao inicializar repositório MongoDB: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void salvar(Produto produto) {
        try {
            logger.debug("Salvando produto com código de barras: {}", produto.getCodigoBarras());
            
            ProdutoDocument document = mapper.toDocument(produto);
            Document bsonDoc = document.toDocument();
            
            collection.replaceOne(
                Filters.eq("codigoBarras", produto.getCodigoBarras()),
                bsonDoc,
                new ReplaceOptions().upsert(true)
            );
            logger.info("Produto atualizado com sucesso: {}", produto.getCodigoBarras());

        } catch (Exception e) {
            logger.error("Erro ao salvar produto {}: {}", produto.getCodigoBarras(), e.getMessage(), e);
            throw new RuntimeException("Erro ao salvar produto: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Optional<Produto> buscarPorCodigoBarras(String codigoBarras) {
        try {
            logger.debug("Buscando produto por código de barras: {}", codigoBarras);
            
            Document doc = collection.find(Filters.eq("codigoBarras", codigoBarras)).first();
            
            if (doc != null) {
                ProdutoDocument produtoDoc = ProdutoDocument.fromDocument(doc);
                Produto produto = mapper.toDomain(produtoDoc);
                logger.info("Produto encontrado: {} - {}", codigoBarras, produto.getNome());
                return Optional.of(produto);
            } else {
                logger.debug("Produto não encontrado para código de barras: {}", codigoBarras);
                return Optional.empty();
            }
            
        } catch (Exception e) {
            logger.error("Erro ao buscar produto {}: {}", codigoBarras, e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar produto: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean existePorCodigoBarras(String codigoBarras) {
        try {
            logger.debug("Verificando existência do produto: {}", codigoBarras);
            
            long count = collection.countDocuments(Filters.eq("codigoBarras", codigoBarras));
            boolean existe = count > 0;
            
            logger.debug("Produto {} existe: {}", codigoBarras, existe);
            return existe;
            
        } catch (Exception e) {
            logger.error("Erro ao verificar existência do produto {}: {}", codigoBarras, e.getMessage(), e);
            throw new RuntimeException("Erro ao verificar existência do produto: " + e.getMessage(), e);
        }
    }
    
 @Override
    public void remover(String codigoBarras) {
        try {
            logger.debug("Removendo produto: {}", codigoBarras);
            
            collection.deleteOne(Filters.eq("codigoBarras", codigoBarras));
            
            logger.info("Produto removido com sucesso: {}", codigoBarras);
            
        } catch (Exception e) {
            logger.error("Erro ao remover produto {}: {}", codigoBarras, e.getMessage(), e);
            throw new RuntimeException("Erro ao remover produto: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void atualizar(Produto produto) {
        try {
            logger.debug("Atualizando produto: {}", produto.getCodigoBarras());
            
            ProdutoDocument document = mapper.toDocument(produto);
            Document bsonDoc = document.toDocument();
            
            collection.replaceOne(
                Filters.eq("codigoBarras", produto.getCodigoBarras()),
                bsonDoc
            );
            
            logger.info("Produto atualizado com sucesso: {}", produto.getCodigoBarras());
            
        } catch (Exception e) {
            logger.error("Erro ao atualizar produto {}: {}", produto.getCodigoBarras(), e.getMessage(), e);
            throw new RuntimeException("Erro ao atualizar produto: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Produto> buscarTodos() {
        try {
            logger.debug("Buscando todos os produtos...");
            List<Produto> produtos = new ArrayList<>();
            for (Document doc : collection.find()) {
                ProdutoDocument produtoDoc = ProdutoDocument.fromDocument(doc);
                produtos.add(mapper.toDomain(produtoDoc));
            }
            logger.info("Encontrados {} produtos.", produtos.size());
            return produtos;
        } catch (Exception e) {
            logger.error("Erro ao buscar todos os produtos: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar todos os produtos: " + e.getMessage(), e);
        }
    }
}