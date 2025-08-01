package com.lucasmks.api.filter;

import static spark.Spark.*;

public class CorsFilters {
    public static void enableCORS() {
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
            response.header("Access-Control-Request-Method", "GET,POST,PUT,DELETE,OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type,Authorization");
            // Note: O cabeçalho Access-Control-Allow-Headers acima é o que os clientes podem enviar.
            // O header Access-Control-Allow-Origin deve ser definido apenas uma vez.
            // O header Access-Control-Request-Method e Access-Control-Allow-Headers já foram definidos no options.
            // Aqui podemos adicionar apenas os headers que queremos para todas as requisições.
        });
    }
}