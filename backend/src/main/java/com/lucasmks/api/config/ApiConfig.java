package com.lucasmks.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static spark.Spark.*;

public class ApiConfig {
    private static final Logger logger = LoggerFactory.getLogger(ApiConfig.class);

    private static final int DEFAULT_PORT = 4567;
    private static final int MAX_THREADS = 8;
    private static final int MIN_THREADS = 2;
    private static final int IDLE_TIMEOUT_MILLIS = 30000;

    public static void configure() {
        logger.info("Configurando servidor HTTP...");

        port(DEFAULT_PORT);
        threadPool(MAX_THREADS, MIN_THREADS, IDLE_TIMEOUT_MILLIS);

        // Opcional: Configurar tratamento de exceções globais
        exception(Exception.class, (exception, req, res) -> {
            logger.error("Ocorreu um erro não tratado na rota {}: {}", req.pathInfo(), exception.getMessage(), exception);
            res.status(500);
            res.body("{\"message\": \"Erro interno do servidor.\"}");
            res.type("application/json");
        });

        logger.info("Configurações do servidor aplicadas (porta: {})", DEFAULT_PORT);
    }
}