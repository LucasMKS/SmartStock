package com.lucasmks.infrastructure.factory;

import com.lucasmks.domain.repository.ProdutoRepository;
import com.lucasmks.domain.repository.MovimentacaoRepository;
import com.lucasmks.domain.usecase.*;
import com.lucasmks.infrastructure.persistence.mapper.ProdutoMapper;
import com.lucasmks.infrastructure.persistence.mapper.MovimentacaoMapper;
import com.lucasmks.infrastructure.persistence.repository.ProdutoMongoRepositoryImpl;
import com.lucasmks.infrastructure.persistence.repository.MovimentacaoMongoRepositoryImpl;

public class ApplicationFactory {

    // Mappers
    private static ProdutoMapper produtoMapper;
    private static MovimentacaoMapper movimentacaoMapper;

    // Repositories
    private static ProdutoRepository produtoRepository;
    private static MovimentacaoRepository movimentacaoRepository;

    // Use Cases
    private static CadastrarProdutoUseCase cadastrarProdutoUseCase;
    private static BuscarProdutoUseCase buscarProdutoUseCase;
    private static AdicionarEstoqueUseCase adicionarEstoqueUseCase;
    private static RemoverEstoqueUseCase removerEstoqueUseCase;
    private static ConsultarHistoricoMovimentacaoUseCase consultarHistoricoMovimentacaoUseCase;
    private static ListarTodosProdutosUseCase listarTodosProdutosUseCase;
    private static EditarProdutoUseCase editarProdutoUseCase;
    private static DeletarProdutoUseCase deletarProdutoUseCase; 
    
    // Mappers
    public static ProdutoMapper getProdutoMapper() {
        if (produtoMapper == null) {
            produtoMapper = new ProdutoMapper();
        }
        return produtoMapper;
    }

    public static MovimentacaoMapper getMovimentacaoMapper() {
        if (movimentacaoMapper == null) {
            movimentacaoMapper = new MovimentacaoMapper();
        }
        return movimentacaoMapper;
    }

    // Repositories
    public static ProdutoRepository getProdutoRepository() {
        if (produtoRepository == null) {
            produtoRepository = new ProdutoMongoRepositoryImpl(getProdutoMapper());
        }
        return produtoRepository;
    }

    public static MovimentacaoRepository getMovimentacaoRepository() {
        if (movimentacaoRepository == null) {
            movimentacaoRepository = new MovimentacaoMongoRepositoryImpl(getMovimentacaoMapper());
        }
        return movimentacaoRepository;
    }

    // Use Cases
    public static CadastrarProdutoUseCase getCadastrarProdutoUseCase() {
        if (cadastrarProdutoUseCase == null) {
            cadastrarProdutoUseCase = new CadastrarProdutoUseCase(getProdutoRepository());
        }
        return cadastrarProdutoUseCase;
    }

    public static BuscarProdutoUseCase getBuscarProdutoUseCase() {
        if (buscarProdutoUseCase == null) {
            buscarProdutoUseCase = new BuscarProdutoUseCase(getProdutoRepository());
        }
        return buscarProdutoUseCase;
    }

    public static AdicionarEstoqueUseCase getAdicionarEstoqueUseCase() {
        if (adicionarEstoqueUseCase == null) {
            adicionarEstoqueUseCase = new AdicionarEstoqueUseCase(getProdutoRepository(), getMovimentacaoRepository());
        }
        return adicionarEstoqueUseCase;
    }

    public static RemoverEstoqueUseCase getRemoverEstoqueUseCase() {
        if (removerEstoqueUseCase == null) {
            removerEstoqueUseCase = new RemoverEstoqueUseCase(getProdutoRepository(), getMovimentacaoRepository());
        }
        return removerEstoqueUseCase;
    }

    public static ConsultarHistoricoMovimentacaoUseCase getConsultarHistoricoMovimentacaoUseCase() {
        if (consultarHistoricoMovimentacaoUseCase == null) {
            consultarHistoricoMovimentacaoUseCase = new ConsultarHistoricoMovimentacaoUseCase(getProdutoRepository(), getMovimentacaoRepository());
        }
        return consultarHistoricoMovimentacaoUseCase;
    }

    public static ListarTodosProdutosUseCase getListarTodosProdutosUseCase() {
        if (listarTodosProdutosUseCase == null) {
            listarTodosProdutosUseCase = new ListarTodosProdutosUseCase(getProdutoRepository());
        }
        return listarTodosProdutosUseCase;
    }

    public static EditarProdutoUseCase getEditarProdutoUseCase() {
        if (editarProdutoUseCase == null) {
            editarProdutoUseCase = new EditarProdutoUseCase(getProdutoRepository());
        }
        return editarProdutoUseCase;
    }

    public static DeletarProdutoUseCase getDeletarProdutoUseCase() {
        if (deletarProdutoUseCase == null) {
            deletarProdutoUseCase = new DeletarProdutoUseCase(getProdutoRepository());
        }
        return deletarProdutoUseCase;
    }

    public static void resetFactory() {
        produtoRepository = null;
        movimentacaoRepository = null;
        cadastrarProdutoUseCase = null;
        buscarProdutoUseCase = null;
        adicionarEstoqueUseCase = null;
        removerEstoqueUseCase = null;
        consultarHistoricoMovimentacaoUseCase = null;
        listarTodosProdutosUseCase = null;
        editarProdutoUseCase = null;
        deletarProdutoUseCase = null;
        produtoMapper = null;
    }
}