package com.lucasmks.infrastructure.database;

import com.lucasmks.domain.model.Produto;
import com.lucasmks.infrastructure.persistence.document.MovimentacaoDocument;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;


public class MongoConnection {

    private static final Logger logger = LoggerFactory.getLogger(MongoConnection.class);

    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static boolean isConnected = false;

    private static final String CONNECTION_STRING = "mongodb://admin:password@localhost:27017"; // String de conexão com usuário/senha
    private static final String DATABASE_NAME = "estoque_db";

    private MongoConnection() {}

    // Método de inicialização separado para melhor controle
    public static void initializeConnection() {
        if (mongoClient == null) { // Verifica se já não está inicializado
            synchronized (MongoConnection.class) {
                if (mongoClient == null) {
                    try {
                        logger.info("Iniciando conexão com MongoDB...");
                        logger.debug("String de conexão: {}", CONNECTION_STRING.replaceAll("password", "***"));
            
                        CodecProvider pojoCodecProvider = PojoCodecProvider.builder()
                                .register(Produto.class, MovimentacaoDocument.class)
                                .build();

                        CodecRegistry pojoCodecRegistry = fromRegistries(
                                MongoClientSettings.getDefaultCodecRegistry(),
                                fromProviders(pojoCodecProvider)
                        );

                        MongoClientSettings settings = MongoClientSettings.builder()
                                .applyConnectionString(new ConnectionString(CONNECTION_STRING))
                                .codecRegistry(pojoCodecRegistry)
                                .build();


                        mongoClient = MongoClients.create(settings);
                        database = mongoClient.getDatabase(DATABASE_NAME);

                        // Testa a conexão 
                        logger.debug("Testando conexão com ping...");
                        database.runCommand(new org.bson.Document("ping", 1));
                        isConnected = true;

                        logger.info("Conexão com MongoDB estabelecida e banco de dados '{}' selecionado.", DATABASE_NAME);
                    } catch (Exception e) {
                        logger.error("Erro ao conectar ao MongoDB: {}", e.getMessage());
                        isConnected = false;
                        throw new RuntimeException("Falha ao inicializar conexão com MongoDB", e);
                    }
                }
            }
        }
    }

    public static MongoDatabase getDatabase() {
        if (!isConnected) { // Garante que a conexão foi inicializada
            initializeConnection();
        }
        return database;
    }

    public static boolean isConnected() {
        return isConnected;
    }

    public static void closeConnection() {
        if (mongoClient != null) {
            logger.info("Fechando conexão com MongoDB...");
            mongoClient.close();
            mongoClient = null;
            database = null;
            isConnected = false;
            System.out.println("Conexão MongoDB fechada!");
        }
    }
}