package com.lucasmks.api.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder; // Importar GsonBuilder
import com.lucasmks.api.adapter.LocalDateTimeAdapter; // Importar o novo Adapter
import com.lucasmks.api.dto.ApiResponse;
import com.lucasmks.api.service.DashboardService;

import java.time.LocalDateTime; // Importar LocalDateTime

import static spark.Spark.get;
import static spark.Spark.path;

public class DashboardController {
    private final DashboardService dashboardService = new DashboardService();
    // Modificar a inicialização do Gson
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public void setupRoutes() {
        path("/api/dashboard", () -> {
            get("/stats", (req, res) -> {
                res.type("application/json");
                try {
                    Object stats = dashboardService.getDashboardStats();
                    // Esta linha agora funcionará sem erro
                    return gson.toJson(new ApiResponse<>(true, "Estatísticas carregadas com sucesso.", stats));
                } catch (Exception e) {
                    res.status(500);
                    return gson.toJson(new ApiResponse<>(false, "Erro ao carregar estatísticas: " + e.getMessage(), null));
                }
            });
        });
    }
}