package com.lucasmks.api.controller;

import static spark.Spark.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lucasmks.api.dto.ApiResponse;

public class HealthController {

    private final ObjectMapper objectMapper;
    

    public HealthController() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());

    }

    public void setupRoutes() {
        // Configurar content type padrão
        before((request, response) -> {
            response.type("application/json");
        });

       // GET /api/health - Health check
        get("/api/health", this::healthCheck);
        
        // Exception handler
        exception(Exception.class, (exception, request, response) -> {
            response.status(500);
            response.body(toJson(ApiResponse.error("Erro interno do servidor: " + exception.getMessage())));
            exception.printStackTrace();
        });
    }

    private Object healthCheck(spark.Request request, spark.Response response) {
        response.status(200);
        return toJson(ApiResponse.success("API funcionando corretamente", "OK"));
    }

    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            return "{\"success\":false,\"message\":\"Erro ao serializar JSON\"}";
        }
    }
}