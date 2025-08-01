package com.lucasmks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.lucasmks.domain.exception.ProdutoNaoEncontradoException;
import com.lucasmks.domain.model.Produto;
import com.lucasmks.domain.repository.ProdutoRepository;
import com.lucasmks.domain.usecase.BuscarProdutoUseCase;
import com.lucasmks.domain.usecase.CadastrarProdutoUseCase;
import com.lucasmks.infrastructure.repository.fake.ProdutoRepositoryFake;


public class BuscarProdutoUseCaseTest {
    private ProdutoRepository repo;
    private BuscarProdutoUseCase useCase;
    private CadastrarProdutoUseCase cadastroCase;

    @BeforeEach
    void setUp() {
        // Inicializa o repositório fake e o caso de uso antes de cada teste
        repo = new ProdutoRepositoryFake();
        cadastroCase = new CadastrarProdutoUseCase(repo);
        useCase = new BuscarProdutoUseCase(repo);
    }

    @Test
    @DisplayName("Deve retornar produto existente pelo código de barras")
    void deveRetornarProdutoExistente() {
        // Arrange: Produto cadastrado
        Produto produto = new Produto(
            "1234567890123",
            "Café em pó",
            "Alimentos",
            10,
            8.50,
            12.00,
            "Fornecedor X"
        );
        cadastroCase.executar(produto);

        // Act: Consultar pelo código de barras
        Produto produtoConsultado = useCase.executar("1234567890123");

        // Assert: Retorna produto correto
        assertNotNull(produtoConsultado);
        assertEquals("1234567890123", produtoConsultado.getCodigoBarras());
        assertEquals("Café em pó", produtoConsultado.getNome());
        assertEquals("Alimentos", produtoConsultado.getCategoria());
        assertEquals(10, produtoConsultado.getQuantidade());
        assertEquals(8.50, produtoConsultado.getPrecoCusto());
        assertEquals(12.00, produtoConsultado.getPrecoVenda());
        
    }

    @Test
    @DisplayName("Deve lançar exceção se produto não existir")
    void deveRetornarNuloOuErroSeProdutoNaoExistir() {
        // Produto produto = new Produto(
        //     "1234567890123",
        //     "Café em pó",
        //     "Alimentos",
        //     10,
        //     8.50,
        //     12.00,
        //     "Fornecedor X"
        // );
        // cadastroCase.executar(produto);

        assertThrows(ProdutoNaoEncontradoException.class, () -> useCase.executar("1234567890123"));

    }

    @Test
    @DisplayName("Deve buscar automaticamente ao ler código de barras")
    void deveBuscarAutomaticamenteAoLerCodigo() {
        // Arrange: Simular leitura de código de barras - Produto previamente cadastrado
        String codigoBarrasLido = "7891234567890";
        Produto produto = new Produto(
            codigoBarrasLido,
            "Açúcar Cristal",
            "Alimentos",
            25,
            3.50,
            5.00,
            "Fornecedor ABC"
        );
        cadastroCase.executar(produto);

        // Act: Consultar produto automaticamente ao ler código
        Produto produtoEncontrado = useCase.executar(codigoBarrasLido);

        // Assert: Produto retornado automaticamente com dados corretos
        assertNotNull(produtoEncontrado, "Produto deve ser encontrado automaticamente");
        assertEquals(codigoBarrasLido, produtoEncontrado.getCodigoBarras(), "Código de barras deve corresponder ao lido");
        assertEquals("Açúcar Cristal", produtoEncontrado.getNome(), "Nome do produto deve estar correto");
        assertEquals("Alimentos", produtoEncontrado.getCategoria(), "Categoria deve estar correta");
        assertEquals(25, produtoEncontrado.getQuantidade(), "Quantidade deve estar correta");
        assertEquals(3.50, produtoEncontrado.getPrecoCusto(), "Preço de custo deve estar correto");
        assertEquals(5.00, produtoEncontrado.getPrecoVenda(), "Preço de venda deve estar correto");
        assertEquals("Fornecedor ABC", produtoEncontrado.getFornecedor(), "Fornecedor deve estar correto");
    }
} 