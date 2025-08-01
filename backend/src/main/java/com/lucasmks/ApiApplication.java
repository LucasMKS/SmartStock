package com.lucasmks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lucasmks.api.config.ApiConfig;
import com.lucasmks.api.config.CorsConfig;
import com.lucasmks.api.router.ApiRouter;
import com.lucasmks.infrastructure.database.MongoConnection;

import static spark.Spark.*;

public class ApiApplication {

    private static final Logger logger = LoggerFactory.getLogger(ApiApplication.class);

    public static void main(String[] args) {
        logger.info("=== INICIANDO API REST - CONTROLE DE ESTOQUE ===");

        try {
            // 1. Configurações da API
            ApiConfig.configure();

            // 2. Inicializar banco de dados
            initializeDatabase();

            // 3. Habilitar CORS
            CorsConfig.enable();

            // 4. Inicializar e mapear rotas da API
            ApiRouter.setupRoutes();

            // 5. Aguardar inicialização do servidor
            awaitInitialization();

            // 6. Configurar shutdown hook
            setupShutdownHook();

            logger.info("API REST iniciada com sucesso!");
            logger.info("Servidor rodando em: http://localhost:4567");
            logger.info("Health Check disponível em: http://localhost:4567/api/health");
            logger.info("Pressione Ctrl+C para parar...");

        } catch (Exception e) {
            logger.error("Erro fatal ao inicializar a API: {}", e.getMessage(), e);
            System.exit(1); // Encerrar a aplicação com código de erro
        }
    }

    private static void initializeDatabase() {
        logger.info("Tentando inicializar conexão com o banco de dados...");
        try {
            // Forçar inicialização da conexão
            MongoConnection.getDatabase();
            if (MongoConnection.isConnected()) {
                logger.info("Conexão com o banco de dados estabelecida com sucesso!");
            } else {
                // Se getDatabase() não lançar exceção, mas isConnected() for falso
                throw new IllegalStateException("Falha na conexão com o banco de dados, mas sem exceção específica.");
            }
        } catch (Exception e) {
            logger.error("Falha ao conectar com o banco de dados: {}", e.getMessage(), e);
            throw new RuntimeException("Falha na inicialização do banco de dados.", e);
        }
    }

    private static void setupShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Recebido sinal de desligamento. Iniciando shutdown gracefully...");
            stop(); // Parar o Spark
            MongoConnection.closeConnection(); // Fechar conexão MongoDB
            logger.info("Shutdown completo. Recursos liberados.");
        }, "Shutdown-Hook-Thread"));
    }
}