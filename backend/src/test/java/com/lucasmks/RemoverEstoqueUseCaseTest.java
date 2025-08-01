package com.lucasmks;

import org.junit.jupiter.api.Test;

import com.lucasmks.domain.exception.ProdutoNaoEncontradoException;
import com.lucasmks.domain.exception.ValidacaoProdutoException;
import com.lucasmks.domain.model.Movimentacao;
import com.lucasmks.domain.model.Produto;
import com.lucasmks.domain.model.TipoMovimentacao;
import com.lucasmks.domain.repository.ProdutoRepository;
import com.lucasmks.domain.usecase.CadastrarProdutoUseCase;
import com.lucasmks.domain.usecase.RemoverEstoqueUseCase;
import com.lucasmks.infrastructure.repository.fake.MovimentacaoRepositoryFake;
import com.lucasmks.infrastructure.repository.fake.ProdutoRepositoryFake;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

/**
 * Teste TDD para o caso de uso: Saída de Estoque
 * Seguindo princípios SOLID (testando abstrações)
 */
public class RemoverEstoqueUseCaseTest {
    private ProdutoRepository repo;
    private MovimentacaoRepositoryFake movimentacaoRepo;
    private RemoverEstoqueUseCase useCase;
    private CadastrarProdutoUseCase cadastroCase;

    @BeforeEach
    void setUp() {
        // Inicializa o repositório fake e o caso de uso antes de cada teste
        repo = new ProdutoRepositoryFake();
        movimentacaoRepo = new MovimentacaoRepositoryFake();
        useCase = new RemoverEstoqueUseCase(repo, movimentacaoRepo);
        cadastroCase = new CadastrarProdutoUseCase(repo);
    }

    @Test
    @DisplayName("Deve permitir remoção de estoque com produto existente")
    void devePermitirRemoverQuantidadeDoEstoque() {
        // Arrange: Produto existente, quantidade válida
        Produto produto = new Produto(
            "1234567890123",
            "Café em pó",
            "Alimentos",
            10,
            8.50,
            12.00,
            "Fornecedor X"
        );

        // Act: Adicionar entrada
        cadastroCase.executar(produto);

        Produto produto2 = new Produto();
        produto2.setCodigoBarras("1234567890123");
        produto2.setQuantidade(5);

        useCase.executar(produto2.getCodigoBarras(), produto2.getQuantidade(), "Motivo da saída");

        // Assert: Quantidade do estoque aumentada corretamente
        Produto salvo = repo.buscarPorCodigoBarras("1234567890123").orElse(null);
        assertNotNull(salvo, "Produto deveria ter sido salvo.");
        assertEquals(5, salvo.getQuantidade());
    }

    @Test
    @DisplayName("Não deve permitir remoção de estoque com produto insuficiente")
    void naoDevePermitirSaidaComEstoqueInsuficiente() {
        // ARRANGE
        // Primeiro, precisamos de um produto *existente* para testar a validação da quantidade
        Produto produto = new Produto(
            "1234567890123",
            "Café em pó",
            "Alimentos",
            10,
            8.50,
            12.00,
            "Fornecedor X"
        );

        // Act: Adicionar entrada
        cadastroCase.executar(produto);

        // ACT & ASSERT
        assertThrows(ValidacaoProdutoException.class, () -> useCase.executar(produto.getCodigoBarras(), 15, "Teste Estoque insuficiente"),
                "Deveria lançar ValidacaoProdutoException para quantidade insuficiente.");
    }

    @Test
    @DisplayName("Não deve permitir remoção de estoque com produto inexistente")
    void naoDevePermitirSaidaParaProdutoInexistente() {
        // Arrange: Produto não cadastrado
        String codigoBarrasInexistente = "1234567890123";
        int quantidadeRemover = 10;
        String motivo = "Compra teste";
        
        assertThrows(ProdutoNaoEncontradoException.class, () -> {
            useCase.executar(codigoBarrasInexistente, quantidadeRemover, motivo);
        }, "Deveria ter lançado ProdutoNaoEncontradoException para produto inexistente.");

        //: Verifique que nada foi salvo
        Produto salvo = repo.buscarPorCodigoBarras(codigoBarrasInexistente).orElse(null);
        assertNull(salvo, "Produto não deveria existir no repositório após a tentativa de remoção.");
    }

    @Test
    @DisplayName("Deve registrar movimentação de saída no histórico")
    void deveRegistrarMotivoDaSaida() {
        // Arrange: Motivo informado (Venda, ajuste, devolução)
        String codigoBarras = "1234567890123";
        int quantidadeRemover = 10;
        String motivo = "Venda";

        // Act
        repo.salvar(new Produto(codigoBarras, "Produto Teste", "Categoria", 20, 1.0, 2.0, "Fornecedor"));


        useCase.executar(codigoBarras, quantidadeRemover, motivo);

        // Assert: Motivo registrado no histórico
        // Usamos .orElse(null) para o Optional, e then assertNotNull para ter certeza que há uma movimentação
        Movimentacao ultimaMovimentacao = movimentacaoRepo.getUltimaMovimentacao().orElse(null);
        assertNotNull(ultimaMovimentacao, "Deveria haver uma última movimentação.");
        assertEquals(motivo, ultimaMovimentacao.getMotivo(), "O motivo da última movimentação deveria ser 'Venda'.");
        assertEquals(codigoBarras, ultimaMovimentacao.getCodigoBarras(), "O código de barras da movimentação deve ser o do produto.");
        assertEquals(quantidadeRemover, ultimaMovimentacao.getQuantidade(), "A quantidade da movimentação deve ser a removida.");
        assertEquals(TipoMovimentacao.SAIDA, ultimaMovimentacao.getTipo(), "O tipo da movimentação deve ser SAIDA.");
    }
} 