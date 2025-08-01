package com.lucasmks;

import com.lucasmks.domain.exception.ProdutoJaExistenteException;
import com.lucasmks.domain.exception.ValidacaoProdutoException;
import com.lucasmks.domain.model.Produto;
import com.lucasmks.domain.repository.ProdutoRepository;
import com.lucasmks.domain.usecase.CadastrarProdutoUseCase;
import com.lucasmks.infrastructure.repository.fake.ProdutoRepositoryFake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes TDD para o caso de uso: Cadastro de Produto.
 * Seguindo princípios SOLID (testando abstrações).
 */
@DisplayName("CadastrarProdutoUseCase Testes")
public class CadastrarProdutoUseCaseTest {

    private ProdutoRepository repo;
    private CadastrarProdutoUseCase useCase;

    @BeforeEach
    void setUp() {
        // Inicializa o repositório fake e o caso de uso antes de cada teste
        repo = new ProdutoRepositoryFake();
        useCase = new CadastrarProdutoUseCase(repo);
    }

    @Test
    @DisplayName("Deve cadastrar produto com dados válidos")
    void deveCadastrarProdutoComDadosValidos() {
        // ARRANGE
        Produto produto = new Produto(
                "1234567890123",
                "Café em pó",
                "Alimentos",
                10,
                8.50,
                12.00,
                "Fornecedor X"
        );

        // ACT
        useCase.executar(produto);

        // ASSERT
        // Verifica se o produto foi salvo e se os dados estão corretos
        Produto salvo = repo.buscarPorCodigoBarras("1234567890123").orElse(null);
        assertNotNull(salvo, "Produto deveria ter sido salvo.");
        assertEquals("1234567890123", salvo.getCodigoBarras());
        assertEquals("Café em pó", salvo.getNome());
        assertEquals("Alimentos", salvo.getCategoria());
        assertEquals(10, salvo.getQuantidade());
        assertEquals(8.50, salvo.getPrecoCusto());
        assertEquals(12.00, salvo.getPrecoVenda());
        assertEquals("Fornecedor X", salvo.getFornecedor());
    }

    @Test
    @DisplayName("Não deve cadastrar produto com código de barras duplicado")
    void naoDeveCadastrarProdutoComCodigoDeBarrasDuplicado() {
        // ARRANGE
        // Cadastra um produto para simular duplicidade
        Produto produtoExistente = new Produto(
                "9876543210987",
                "Arroz Tipo 1",
                "Alimentos",
                5,
                5.00,
                7.50,
                "Fornecedor Y"
        );
        useCase.executar(produtoExistente); // Este cadastro deveria ser feito pelo repositório fake diretamente para simular o estado inicial

        Produto produtoDuplicado = new Produto(
                "9876543210987", // Código de barras duplicado
                "Arroz Tipo 2",
                "Alimentos",
                10,
                5.50,
                8.00,
                "Fornecedor Y"
        );

        // ACT & ASSERT
        // Espera-se uma exceção ProdutoJaExistenteException
        assertThrows(ProdutoJaExistenteException.class, () -> useCase.executar(produtoDuplicado),
                "Deveria lançar ProdutoJaExistenteException para código duplicado.");
    }

    @Test
    @DisplayName("Não deve cadastrar produto com código de barras vazio ou nulo")
    void naoDeveCadastrarProdutoComCodigoDeBarrasInvalido() {
        // ARRANGE
        Produto produtoVazio = new Produto(
                "", // Código de barras vazio
                "Refrigerante", "Bebidas", 20, 2.00, 4.00, "Fornecedor Z"
        );
        Produto produtoNulo = new Produto(
                null, // Código de barras nulo
                "Salgadinho", "Lanches", 15, 1.50, 3.00, "Fornecedor W"
        );

        // ACT & ASSERT
        assertThrows(ValidacaoProdutoException.class, () -> useCase.executar(produtoVazio),
                "Deveria lançar ValidacaoProdutoException para código de barras vazio.");
        assertThrows(ValidacaoProdutoException.class, () -> useCase.executar(produtoNulo),
                "Deveria lançar ValidacaoProdutoException para código de barras nulo.");
    }

    @Test
    @DisplayName("Não deve cadastrar produto com nome vazio ou nulo")
    void naoDeveCadastrarProdutoComNomeInvalido() {
        // ARRANGE
        Produto produtoNomeVazio = new Produto(
                "1112223334445",
                "", // Nome vazio
                "Limpeza", 5, 10.00, 15.00, "Fornecedor A"
        );
        Produto produtoNomeNulo = new Produto(
                "5556667778889",
                null, // Nome nulo
                "Higiene", 8, 7.00, 10.00, "Fornecedor B"
        );

        // ACT & ASSERT
        assertThrows(ValidacaoProdutoException.class, () -> useCase.executar(produtoNomeVazio),
                "Deveria lançar ValidacaoProdutoException para nome vazio.");
        assertThrows(ValidacaoProdutoException.class, () -> useCase.executar(produtoNomeNulo),
                "Deveria lançar ValidacaoProdutoException para nome nulo.");
    }

    @Test
    @DisplayName("Não deve cadastrar produto com quantidade inicial negativa")
    void naoDeveCadastrarProdutoComQuantidadeNegativa() {
        // ARRANGE
        Produto produtoQuantNegativa = new Produto(
                "1231231231231",
                "Água Mineral",
                "Bebidas",
                -1, // Quantidade negativa
                1.00,
                2.00,
                "Fornecedor C"
        );

        // ACT & ASSERT
        assertThrows(ValidacaoProdutoException.class, () -> useCase.executar(produtoQuantNegativa),
                "Deveria lançar ValidacaoProdutoException para quantidade negativa.");
    }

    @Test
    @DisplayName("Não deve cadastrar produto com preço de custo negativo")
    void naoDeveCadastrarProdutoComPrecoCustoNegativo() {
        // ARRANGE
        Produto produtoPrecoCustoNegativo = new Produto(
                "4564564564564",
                "Chocolate",
                "Doces",
                10,
                -0.50, // Preço de custo negativo
                5.00,
                "Fornecedor D"
        );

        // ACT & ASSERT
        assertThrows(ValidacaoProdutoException.class, () -> useCase.executar(produtoPrecoCustoNegativo),
                "Deveria lançar ValidacaoProdutoException para preço de custo negativo.");
    }

    @Test
    @DisplayName("Não deve cadastrar produto com preço de venda negativo")
    void naoDeveCadastrarProdutoComPrecoVendaNegativo() {
        // ARRANGE
        Produto produtoPrecoVendaNegativo = new Produto(
                "7897897897897",
                "Biscoito",
                "Lanches",
                10,
                1.00,
                -2.00, // Preço de venda negativo
                "Fornecedor E"
        );

        // ACT & ASSERT
        assertThrows(ValidacaoProdutoException.class, () -> useCase.executar(produtoPrecoVendaNegativo),
                "Deveria lançar ValidacaoProdutoException para preço de venda negativo.");
    }
}