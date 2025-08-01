package com.lucasmks.api.router;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lucasmks.api.controller.DashboardController;
import com.lucasmks.api.controller.HealthController;
import com.lucasmks.api.controller.HistoricoMovimentacaoController;
import com.lucasmks.api.controller.ProdutoController;

public class ApiRouter {
    private static final Logger logger = LoggerFactory.getLogger(ApiRouter.class);

    public static void setupRoutes() {
        logger.info("Configurando rotas da API...");

        // Inicializar e configurar cada controlador
        new HealthController().setupRoutes();
        new ProdutoController().setupRoutes();
        new HistoricoMovimentacaoController().setupRoutes();
        new DashboardController().setupRoutes();

        logger.info("Todas as rotas da API configuradas.");
    }
}