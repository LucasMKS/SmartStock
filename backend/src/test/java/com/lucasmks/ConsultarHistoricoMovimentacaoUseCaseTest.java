package com.lucasmks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.lucasmks.domain.model.Movimentacao;
import com.lucasmks.domain.model.Produto;
import com.lucasmks.domain.model.TipoMovimentacao;
import com.lucasmks.domain.repository.MovimentacaoRepository;
import com.lucasmks.domain.repository.ProdutoRepository;
import com.lucasmks.domain.usecase.AdicionarEstoqueUseCase;
import com.lucasmks.domain.usecase.CadastrarProdutoUseCase;
import com.lucasmks.domain.usecase.ConsultarHistoricoMovimentacaoUseCase;
import com.lucasmks.domain.usecase.RemoverEstoqueUseCase;
import com.lucasmks.infrastructure.repository.fake.MovimentacaoRepositoryFake;
import com.lucasmks.infrastructure.repository.fake.ProdutoRepositoryFake;

/**
 * Teste TDD para o caso de uso: Histórico de Movimentações
 * Seguindo princípios SOLID (testando abstrações)
 */
@DisplayName("ConsultarHistoricoMovimentacaoUseCase Testes")
public class ConsultarHistoricoMovimentacaoUseCaseTest {

    private ProdutoRepository produtoRepo;
    private MovimentacaoRepository movimentacaoRepo;
    private CadastrarProdutoUseCase cadastrarProdutoUseCase; // Para usar no setup
    private AdicionarEstoqueUseCase adicionarEstoqueUseCase; // Para usar no setup
    private RemoverEstoqueUseCase removerEstoqueUseCase; // Para usar no setup
    private ConsultarHistoricoMovimentacaoUseCase consultarHistoricoUseCase;

    private final String CODIGO_PRODUTO_A = "PROD001";
    private final String CODIGO_PRODUTO_B = "PROD002";

    @BeforeEach
    void setUp() {
        // Inicializa o repositório fake e o caso de uso antes de cada teste
        produtoRepo = new ProdutoRepositoryFake();
        movimentacaoRepo = new MovimentacaoRepositoryFake();
        cadastrarProdutoUseCase = new CadastrarProdutoUseCase(produtoRepo); // Passa apenas ProdutoRepository
        adicionarEstoqueUseCase = new AdicionarEstoqueUseCase(produtoRepo, movimentacaoRepo); // Passa ambos
        removerEstoqueUseCase = new RemoverEstoqueUseCase(produtoRepo, movimentacaoRepo); // Passa ambos
        consultarHistoricoUseCase = new ConsultarHistoricoMovimentacaoUseCase(produtoRepo, movimentacaoRepo); // Passa ambos

        // Garante que os repositórios estão limpos antes de cada teste
        ((ProdutoRepositoryFake) produtoRepo).limpar();
        ((MovimentacaoRepositoryFake) movimentacaoRepo).limpar();

        // Prepara produtos no repositório para simular operações
        cadastrarProdutoUseCase.executar(new Produto(CODIGO_PRODUTO_A, "Caneta Azul", "Papelaria", 100, 1.0, 2.0, "Fornecedor X"));
        cadastrarProdutoUseCase.executar(new Produto(CODIGO_PRODUTO_B, "Caderno Espiral", "Papelaria", 50, 5.0, 10.0, "Fornecedor Y"));
    }

        
    @Test
    @DisplayName("Deve registrar movimentação de entrada e listar no histórico")
    void deveRegistrarMovimentacaoDeEntrada() {
        // Arrange: Entrada de produto
        String codigoBarras = "1234567890123";
        int quantidadeAdicionar = 10;
        String motivo = "Venda";

        // Act
        cadastrarProdutoUseCase.executar(new Produto(codigoBarras, "Produto Teste", "Categoria", 20, 1.0, 2.0, "Fornecedor"));
        
        // Act: Registrar movimentação
        adicionarEstoqueUseCase.executar(codigoBarras, quantidadeAdicionar, motivo);

        // Assert: Histórico contém movimentação de entrada
        assertTrue(consultarHistoricoUseCase.buscarTodasMovimentacoes().stream()
                .anyMatch(m -> m.getCodigoBarras().equals(codigoBarras)
                        && m.getQuantidade() == quantidadeAdicionar
                        && m.getMotivo().equals(motivo)
                        && m.getTipo() == TipoMovimentacao.ENTRADA));
    }

    @Test
    @DisplayName("Deve registrar movimentação de saída e listar no histórico")
    void deveRegistrarMovimentacaoDeSaida() {
        // Arrange: Saída de produto
        String codigoBarras = "1234567890123";
        int quantidadeRemover = 10;
        String motivo = "Venda";

        // Act
        cadastrarProdutoUseCase.executar(new Produto(codigoBarras, "Produto Teste", "Categoria", 20, 1.0, 2.0, "Fornecedor"));

        // Act: Registrar movimentação
        removerEstoqueUseCase.executar(codigoBarras, quantidadeRemover, motivo);

        // Assert: Histórico contém movimentação de saida
        assertTrue(consultarHistoricoUseCase.buscarTodasMovimentacoes().stream()
                .anyMatch(m -> m.getCodigoBarras().equals(codigoBarras)
                        && m.getQuantidade() == quantidadeRemover
                        && m.getMotivo().equals(motivo)
                        && m.getTipo() == TipoMovimentacao.SAIDA));
    }

    @Test
    @DisplayName("Deve listar movimentações por período corretamente")
    void deveListarMovimentacoesPorPeriodo() {
        // Arrange: Movimentações em diferentes datas
        // Movimentação DENTRO do período
        LocalDateTime dataDentro1 = LocalDateTime.of(2025, 7, 10, 10, 0, 0); // Exemplo: 10 de Julho de 2025
        Movimentacao movDentro1 = new Movimentacao("PROD001", 20, TipoMovimentacao.ENTRADA, "Compra", dataDentro1);
        movimentacaoRepo.salvar(movDentro1);

        // Movimentação DENTRO do período
        LocalDateTime dataDentro2 = LocalDateTime.of(2025, 7, 15, 14, 30, 0); // Exemplo: 15 de Julho de 2025
        Movimentacao movDentro2 = new Movimentacao("PROD002", 5, TipoMovimentacao.SAIDA, "Venda", dataDentro2);
        movimentacaoRepo.salvar(movDentro2);

        // Movimentação FORA do período (antes)
        LocalDateTime dataForaAntes = LocalDateTime.of(2025, 7, 5, 8, 0, 0); // Exemplo: 5 de Julho de 2025
        Movimentacao movForaAntes = new Movimentacao("PROD003", 30, TipoMovimentacao.ENTRADA, "Ajuste Antigo", dataForaAntes);
        movimentacaoRepo.salvar(movForaAntes);

        // Movimentação FORA do período (depois)
        LocalDateTime dataForaDepois = LocalDateTime.of(2025, 7, 25, 18, 0, 0); // Exemplo: 25 de Julho de 2025
        Movimentacao movForaDepois = new Movimentacao("PROD004", 10, TipoMovimentacao.SAIDA, "Descarte Futuro", dataForaDepois);
        movimentacaoRepo.salvar(movForaDepois);

        // Definir o período de consulta (exatamente onde queremos buscar)
        LocalDateTime inicioPeriodo = LocalDateTime.of(2025, 7, 10, 0, 0, 0); // Começo do dia 10
        LocalDateTime fimPeriodo = LocalDateTime.of(2025, 7, 20, 23, 59, 59);   // Fim do dia 20

        // ACT: Consultar por período
        List<Movimentacao> movimentacoesNoPeriodo = consultarHistoricoUseCase.buscarMovimentacoesPorPeriodo(inicioPeriodo, fimPeriodo);

        // ASSERT: Retorna apenas as movimentações do período correto
        assertNotNull(movimentacoesNoPeriodo, "A lista de movimentações não deve ser nula.");
        assertEquals(2, movimentacoesNoPeriodo.size(), "Deveria haver exatamente 2 movimentações no período especificado.");

        // Verificar se as movimentações esperadas estão presentes
        assertTrue(movimentacoesNoPeriodo.stream().anyMatch(m -> m.getCodigoBarras().equals("PROD001")),
                "Movimentação com PROD001 deveria estar no período.");
        assertTrue(movimentacoesNoPeriodo.stream().anyMatch(m -> m.getCodigoBarras().equals("PROD002")),
                "Movimentação com PROD002 deveria estar no período.");

        // Verificar que as movimentações FORA do período NÃO estão presentes
        assertFalse(movimentacoesNoPeriodo.stream().anyMatch(m -> m.getCodigoBarras().equals("PROD003")),
                "Movimentação com PROD003 (anterior) NÃO deveria estar no período.");
        assertFalse(movimentacoesNoPeriodo.stream().anyMatch(m -> m.getCodigoBarras().equals("PROD004")),
                "Movimentação com ID4 (futura) NÃO deveria estar no período.");
    }


    @Test
    void deveListarMovimentacoesPorProduto() {
        // Arrange: Movimentações de diferentes produtos
        cadastrarProdutoUseCase.executar(new Produto("ID1", "Produto Teste", "Categoria", 20, 1.0, 2.0, "Fornecedor"));
        adicionarEstoqueUseCase.executar("ID1", 10, "Compra");

        cadastrarProdutoUseCase.executar(new Produto("ID2", "Produto Teste Venda", "Categoria", 20, 1.0, 2.0, "Fornecedor"));
        removerEstoqueUseCase.executar("ID2", 5, "Venda");
        
        // Act: Consultar por produto
        List<Movimentacao> movimentacoesProduto1 = consultarHistoricoUseCase.buscarMovimentacoesPorProduto("ID1");
        List<Movimentacao> movimentacoesProduto2 = consultarHistoricoUseCase.buscarMovimentacoesPorProduto("ID2");

        // Assert: Retorna apenas movimentações do produto filtrado
        assertNotNull(movimentacoesProduto1, "Movimentações do produto 1 não devem ser nulas.");
        assertEquals(1, movimentacoesProduto1.size(), "Deveria haver 1 movimentação para o produto 1.");
        assertTrue(movimentacoesProduto1.stream().anyMatch(m -> m.getCodigoBarras().equals("ID1")),
                "Movimentação do produto 1 deveria estar presente.");
                
        assertNotNull(movimentacoesProduto2, "Movimentações do produto 2 não devem ser nulas.");
        assertEquals(1, movimentacoesProduto2.size(), "Deveria haver 1 movimentação para o produto 2.");
        assertTrue(movimentacoesProduto2.stream().anyMatch(m -> m.getCodigoBarras().equals("ID2")),
                "Movimentação do produto 2 deveria estar presente.");
    }

    @Test
    void deveListarMovimentacoesPorMotivo() {
        // Arrange: Movimentações com diferentes motivos
        cadastrarProdutoUseCase.executar(new Produto("ID1", "Produto Teste", "Categoria", 20, 1.0, 2.0, "Fornecedor"));
        adicionarEstoqueUseCase.executar("ID1", 10, "Compra");

        cadastrarProdutoUseCase.executar(new Produto("ID2", "Produto Teste Venda", "Categoria", 20, 1.0, 2.0, "Fornecedor"));
        removerEstoqueUseCase.executar("ID2", 5, "Venda");

        // Act: Consultar por motivo (venda, ajuste, devolução)
        List<Movimentacao> movimentacaoPorCompra = consultarHistoricoUseCase.buscarMovimentacoesPorMotivo("Compra");
        List<Movimentacao> movimentacaoPorVenda = consultarHistoricoUseCase.buscarMovimentacoesPorMotivo("Venda");

        // Assert: Retorna apenas movimentações do motivo filtrado
        assertNotNull(movimentacaoPorCompra, "Movimentações por compra não devem ser nulas.");
        assertEquals(1, movimentacaoPorCompra.size(), "Deveria haver 1 movimentação para o motivo 'Compra'.");
        assertTrue(movimentacaoPorCompra.stream().anyMatch(m -> m.getMotivo().equals("Compra")),
                "Movimentação com motivo 'Compra' deveria estar presente.");

        assertNotNull(movimentacaoPorVenda, "Movimentações por venda não devem ser nulas.");
        assertEquals(1, movimentacaoPorVenda.size(), "Deveria haver 1 movimentação para o motivo 'Venda'.");
        assertTrue(movimentacaoPorVenda.stream().anyMatch(m -> m.getMotivo().equals("Venda")),
                "Movimentação com motivo 'Venda' deveria estar presente.");

    }

    @Test
    @DisplayName("Deve buscar movimentações por tipo (entrada/saída)")
    void deveBuscarMovimentacoesPorTipo() {
        // Arrange: Movimentações de diferentes tipos
        cadastrarProdutoUseCase.executar(new Produto("ID1", "Produto Teste", "Categoria", 20, 1.0, 2.0, "Fornecedor"));
        adicionarEstoqueUseCase.executar("ID1", 10, "Compra");

        cadastrarProdutoUseCase.executar(new Produto("ID2", "Produto Teste Venda", "Categoria", 20, 1.0, 2.0, "Fornecedor"));
        removerEstoqueUseCase.executar("ID2", 5, "Venda");

        // Act: Consultar por tipo (entrada e saída)
        List<Movimentacao> movimentacoesEntrada = consultarHistoricoUseCase.buscarMovimentacoesPorTipo(TipoMovimentacao.ENTRADA);
        List<Movimentacao> movimentacoesSaida = consultarHistoricoUseCase.buscarMovimentacoesPorTipo(TipoMovimentacao.SAIDA);

        // Assert: Retorna apenas movimentações do tipo filtrado
        assertNotNull(movimentacoesEntrada, "Movimentações de entrada não devem ser nulas.");
        assertEquals(1, movimentacoesEntrada.size(), "Deveria haver 1 movimentação de entrada.");
        assertTrue(movimentacoesEntrada.stream().anyMatch(m -> m.getTipo() == TipoMovimentacao.ENTRADA),
                "Movimentação de entrada deveria estar presente.");

        assertNotNull(movimentacoesSaida, "Movimentações de saída não devem ser nulas.");
        assertEquals(1, movimentacoesSaida.size(), "Deveria haver 1 movimentação de saída.");
        assertTrue(movimentacoesSaida.stream().anyMatch(m -> m.getTipo() == TipoMovimentacao.SAIDA),
                "Movimentação de saída deveria estar presente.");
    }
} 