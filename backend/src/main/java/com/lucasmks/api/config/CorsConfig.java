package com.lucasmks.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static spark.Spark.*;

public class CorsConfig {
    private static final Logger logger = LoggerFactory.getLogger(CorsConfig.class);

    public static void enable() {
        logger.info("Habilitando CORS...");

        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });

        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization, Accept");
            response.header("Access-Control-Allow-Credentials", "true"); // Se você precisar de credenciais
        });

        logger.info("CORS habilitado para todas as rotas.");
    }
}