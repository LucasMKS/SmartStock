package com.lucasmks;

import org.junit.jupiter.api.Test;

import com.lucasmks.domain.exception.ProdutoNaoEncontradoException;
import com.lucasmks.domain.exception.ValidacaoProdutoException;
import com.lucasmks.domain.model.Movimentacao;
import com.lucasmks.domain.model.Produto;
import com.lucasmks.domain.model.TipoMovimentacao;
import com.lucasmks.domain.repository.ProdutoRepository;
import com.lucasmks.domain.usecase.AdicionarEstoqueUseCase;
import com.lucasmks.domain.usecase.CadastrarProdutoUseCase;
import com.lucasmks.infrastructure.repository.fake.MovimentacaoRepositoryFake;
import com.lucasmks.infrastructure.repository.fake.ProdutoRepositoryFake;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

/**
 * Teste TDD para o caso de uso: Entrada de Estoque
 * Seguindo princípios SOLID (testando abstrações)
 */
public class AdicionarEstoqueUseCaseTest {

    private ProdutoRepository repo;
    private MovimentacaoRepositoryFake movimentacaoRepo;
    private AdicionarEstoqueUseCase useCase;
    private CadastrarProdutoUseCase cadastroCase;

    @BeforeEach
    void setUp() {
        // Inicializa o repositório fake e o caso de uso antes de cada teste
        repo = new ProdutoRepositoryFake();
        movimentacaoRepo = new MovimentacaoRepositoryFake();
        useCase = new AdicionarEstoqueUseCase(repo, movimentacaoRepo);
        cadastroCase = new CadastrarProdutoUseCase(repo);
    }

    @Test
    @DisplayName("Deve adicionar quantidade ao estoque")
    void deveAdicionarQuantidadeAoEstoque() {
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
        produto2.setQuantidade(20);

        useCase.executar(produto2.getCodigoBarras(), produto2.getQuantidade(), "Motivo da entrada");

        // Assert: Quantidade do estoque aumentada corretamente
        Produto salvo = repo.buscarPorCodigoBarras("1234567890123").orElse(null);
        assertNotNull(salvo, "Produto deveria ter sido salvo.");
        assertEquals(30, salvo.getQuantidade());
    }

    @Test
    @DisplayName("Não deve adicionar estoque para produto inexistente")
    void naoDeveAdicionarEstoqueParaProdutoInexistente() {
        // Arrange: Produto não cadastrado
        String codigoBarrasInexistente = "1234567890123";
        int quantidadeAdicionar = 10;
        String motivo = "Compra teste";
        
        assertThrows(ProdutoNaoEncontradoException.class, () -> {
            useCase.executar(codigoBarrasInexistente, quantidadeAdicionar, motivo);
        }, "Deveria ter lançado ProdutoNaoEncontradoException para produto inexistente.");

        //: Verifique que nada foi salvo
        Produto salvo = repo.buscarPorCodigoBarras(codigoBarrasInexistente).orElse(null);
        assertNull(salvo, "Produto não deveria existir no repositório após a tentativa de adição.");
    }

    @Test
    @DisplayName("Não deve adicionar quantidade negativa ou zero ao estoque")
    void naoDeveAdicionarQuantidadeNegativa() {
        // ARRANGE
        // Primeiro, precisamos de um produto *existente* para testar a validação da quantidade
        Produto produtoExistente = new Produto("1234567890123", "Caneta", "Escrita", 50, 1.0, 2.0, "Fornecedor Z");
        repo.salvar(produtoExistente); // Salva o produto para que ele exista

        String codigoExistente = "1234567890123";
        String motivo = "Teste Quantidade";

        // ACT & ASSERT
        assertThrows(ValidacaoProdutoException.class, () -> useCase.executar(codigoExistente, -10, motivo),
                "Deveria lançar ValidacaoProdutoException para quantidade negativa.");
        assertThrows(ValidacaoProdutoException.class, () -> useCase.executar(codigoExistente, 0, motivo),
                "Deveria lançar ValidacaoProdutoException para quantidade zero.");
    }

    @Test
    void deveRegistrarMotivoDaEntrada() {
        // Arrange: Motivo informado (compra, ajuste, devolução)
        String codigoBarras = "1234567890123";
        int quantidadeAdicionar = 10;
        String motivo = "Compra";

        // Act
        repo.salvar(new Produto(codigoBarras, "Produto Teste", "Categoria", 0, 1.0, 2.0, "Fornecedor"));


        useCase.executar(codigoBarras, quantidadeAdicionar, motivo);

        // Assert: Motivo registrado no histórico
        // Usamos .orElse(null) para o Optional, e then assertNotNull para ter certeza que há uma movimentação
        Movimentacao ultimaMovimentacao = movimentacaoRepo.getUltimaMovimentacao().orElse(null);
        assertNotNull(ultimaMovimentacao, "Deveria haver uma última movimentação.");
        assertEquals(motivo, ultimaMovimentacao.getMotivo(), "O motivo da última movimentação deveria ser 'Compra'.");
        assertEquals(codigoBarras, ultimaMovimentacao.getCodigoBarras(), "O código de barras da movimentação deve ser o do produto.");
        assertEquals(quantidadeAdicionar, ultimaMovimentacao.getQuantidade(), "A quantidade da movimentação deve ser a adicionada.");
        assertEquals(TipoMovimentacao.ENTRADA, ultimaMovimentacao.getTipo(), "O tipo da movimentação deve ser ENTRADA.");
    }
} 