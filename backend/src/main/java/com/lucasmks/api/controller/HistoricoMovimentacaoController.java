package com.lucasmks.api.controller;

import static spark.Spark.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lucasmks.api.dto.ApiResponse;
import com.lucasmks.api.dto.MovimentacaoResponse; // <-- Importar
import com.lucasmks.api.mapper.MovimentacaoApiMapper; // <-- Importar
import com.lucasmks.domain.model.Movimentacao;
import com.lucasmks.domain.model.TipoMovimentacao; // <-- Importar
import com.lucasmks.domain.usecase.ConsultarHistoricoMovimentacaoUseCase; // <-- Importar
import com.lucasmks.infrastructure.factory.ApplicationFactory;

import java.time.LocalDateTime; // <-- Importar
import java.time.format.DateTimeParseException; // <-- Importar para lidar com erros de data
import java.util.List;

public class HistoricoMovimentacaoController {

    private final ObjectMapper objectMapper;
    private final ConsultarHistoricoMovimentacaoUseCase consultarHistoricoUseCase;

    public HistoricoMovimentacaoController() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        // Opcional: Para pretty print em desenvolvimento
        // this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        this.consultarHistoricoUseCase = ApplicationFactory.getConsultarHistoricoMovimentacaoUseCase(); // <-- Injetar
    }

    public void setupRoutes() {
        // Configurar content type padrão
        before((request, response) -> {
            response.type("application/json");
        });

        // GET /api/historico - Listar todas as movimentações
        get("/api/historico", this::buscarTodasMovimentacoes);

        // GET /api/historico/produto/:codigoBarras - Listar movimentações por produto
        get("/api/historico/produto/:codigoBarras", this::buscarMovimentacoesPorProduto);

        // GET /api/historico/periodo?dataInicio=YYYY-MM-DDTHH:MM:SS&dataFim=YYYY-MM-DDTHH:MM:SS
        get("/api/historico/periodo", this::buscarMovimentacoesPorPeriodo);

        // GET /api/historico/motivo/:motivo - Listar movimentações por motivo
        get("/api/historico/motivo/:motivo", this::buscarMovimentacoesPorMotivo);

        // GET /api/historico/tipo/:tipo - Listar movimentações por tipo (ENTRADA/SAIDA)
        get("/api/historico/tipo/:tipo", this::buscarMovimentacoesPorTipo);

        // Exception handler (opcional: mover para classe utilitária de erros global)
        exception(Exception.class, (exception, request, response) -> {
            response.status(500);
            // logger.error("Erro interno do servidor: {}", exception.getMessage(), exception);
            response.body(toJson(ApiResponse.error("Erro interno do servidor: " + exception.getMessage())));
            exception.printStackTrace(); // Apenas para debug em desenvolvimento, remover em produção
        });
    }

    private Object buscarTodasMovimentacoes(spark.Request request, spark.Response response) {
        try {
            List<Movimentacao> movimentacoes = consultarHistoricoUseCase.buscarTodasMovimentacoes();
            List<MovimentacaoResponse> responseList = MovimentacaoApiMapper.toResponseList(movimentacoes);
            response.status(200);
            return toJson(ApiResponse.success("Histórico completo de movimentações", responseList));
        } catch (Exception e) {
            response.status(500);
            return toJson(ApiResponse.error("Erro ao buscar histórico: " + e.getMessage()));
        }
    }

    private Object buscarMovimentacoesPorProduto(spark.Request request, spark.Response response) {
        try {
            String codigoBarras = request.params(":codigoBarras");
            if (codigoBarras == null || codigoBarras.trim().isEmpty()) {
                response.status(400);
                return toJson(ApiResponse.error("Código de barras é obrigatório."));
            }
            List<Movimentacao> movimentacoes = consultarHistoricoUseCase.buscarMovimentacoesPorProduto(codigoBarras);
            List<MovimentacaoResponse> responseList = MovimentacaoApiMapper.toResponseList(movimentacoes);
            response.status(200);
            return toJson(ApiResponse.success("Movimentações para o produto " + codigoBarras, responseList));
        } catch (Exception e) {
            response.status(500);
            return toJson(ApiResponse.error("Erro ao buscar movimentações por produto: " + e.getMessage()));
        }
    }

    private Object buscarMovimentacoesPorPeriodo(spark.Request request, spark.Response response) {
        try {
            String dataInicioStr = request.queryParams("dataInicio");
            String dataFimStr = request.queryParams("dataFim");

            if (dataInicioStr == null || dataInicioStr.trim().isEmpty() || dataFimStr == null || dataFimStr.trim().isEmpty()) {
                response.status(400);
                return toJson(ApiResponse.error("Os parâmetros dataInicio e dataFim são obrigatórios. Formato esperado: YYYY-MM-DDTHH:MM:SS"));
            }

            LocalDateTime dataInicio;
            LocalDateTime dataFim;
            try {
                dataInicio = LocalDateTime.parse(dataInicioStr);
                dataFim = LocalDateTime.parse(dataFimStr);
            } catch (DateTimeParseException e) {
                response.status(400);
                return toJson(ApiResponse.error("Formato de data inválido. Use YYYY-MM-DDTHH:MM:SS. Ex: 2025-07-20T10:30:00"));
            }

            List<Movimentacao> movimentacoes = consultarHistoricoUseCase.buscarMovimentacoesPorPeriodo(dataInicio, dataFim);
            List<MovimentacaoResponse> responseList = MovimentacaoApiMapper.toResponseList(movimentacoes);
            response.status(200);
            return toJson(ApiResponse.success("Movimentações no período", responseList));
        } catch (Exception e) {
            response.status(500);
            return toJson(ApiResponse.error("Erro ao buscar movimentações por período: " + e.getMessage()));
        }
    }

    private Object buscarMovimentacoesPorMotivo(spark.Request request, spark.Response response) {
        try {
            String motivo = request.params(":motivo");
            if (motivo == null || motivo.trim().isEmpty()) {
                response.status(400);
                return toJson(ApiResponse.error("Motivo é obrigatório."));
            }
            List<Movimentacao> movimentacoes = consultarHistoricoUseCase.buscarMovimentacoesPorMotivo(motivo);
            List<MovimentacaoResponse> responseList = MovimentacaoApiMapper.toResponseList(movimentacoes);
            response.status(200);
            return toJson(ApiResponse.success("Movimentações por motivo '" + motivo + "'", responseList));
        } catch (Exception e) {
            response.status(500);
            return toJson(ApiResponse.error("Erro ao buscar movimentações por motivo: " + e.getMessage()));
        }
    }

    private Object buscarMovimentacoesPorTipo(spark.Request request, spark.Response response) {
        try {
            String tipoStr = request.params(":tipo");
            if (tipoStr == null || tipoStr.trim().isEmpty()) {
                response.status(400);
                return toJson(ApiResponse.error("Tipo de movimentação é obrigatório (ENTRADA ou SAIDA)."));
            }
            TipoMovimentacao tipo;
            try {
                tipo = TipoMovimentacao.valueOf(tipoStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                response.status(400);
                return toJson(ApiResponse.error("Tipo de movimentação inválido. Use ENTRADA ou SAIDA."));
            }

            List<Movimentacao> movimentacoes = consultarHistoricoUseCase.buscarMovimentacoesPorTipo(tipo);
            List<MovimentacaoResponse> responseList = MovimentacaoApiMapper.toResponseList(movimentacoes);
            response.status(200);
            return toJson(ApiResponse.success("Movimentações do tipo '" + tipo + "'", responseList));
        } catch (Exception e) {
            response.status(500);
            return toJson(ApiResponse.error("Erro ao buscar movimentações por tipo: " + e.getMessage()));
        }
    }

    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            // Este catch deve ser raramente atingido para problemas de serialização
            return "{\"success\":false,\"message\":\"Erro ao serializar JSON\"}";
        }
    }
}