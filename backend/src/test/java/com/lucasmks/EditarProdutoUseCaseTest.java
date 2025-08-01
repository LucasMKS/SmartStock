package com.lucasmks;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lucasmks.domain.exception.ProdutoNaoEncontradoException;
import com.lucasmks.domain.exception.ValidacaoProdutoException;
import com.lucasmks.domain.model.Produto;
import com.lucasmks.domain.repository.ProdutoRepository;
import com.lucasmks.domain.usecase.EditarProdutoUseCase;

@ExtendWith(MockitoExtension.class)
class EditarProdutoUseCaseTest {

    @Mock
    private ProdutoRepository repository;

    private EditarProdutoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new EditarProdutoUseCase(repository);
    }

    @Test
    void deveEditarProdutoComSucesso() {
        // Arrange
        String codigoBarras = "123456789";
        Produto produtoOriginal = new Produto(codigoBarras, "Produto Original", "Categoria", 10, 5.0, 10.0, "Fornecedor");
        Produto produtoAtualizado = new Produto(codigoBarras, "Produto Atualizado", "Nova Categoria", 15, 6.0, 12.0, "Novo Fornecedor");

        when(repository.existePorCodigoBarras(codigoBarras)).thenReturn(true);
        when(repository.buscarPorCodigoBarras(codigoBarras)).thenReturn(java.util.Optional.of(produtoAtualizado));

        // Act
        Produto resultado = useCase.executar(codigoBarras, produtoOriginal);

        // Assert
        assertNotNull(resultado);
        assertEquals("Produto Atualizado", resultado.getNome());
        assertEquals("Nova Categoria", resultado.getCategoria());
        assertEquals(15, resultado.getQuantidade());
        assertEquals(6.0, resultado.getPrecoCusto());
        assertEquals(12.0, resultado.getPrecoVenda());
        assertEquals("Novo Fornecedor", resultado.getFornecedor());
        assertEquals(codigoBarras, resultado.getCodigoBarras());

        verify(repository).existePorCodigoBarras(codigoBarras);
        verify(repository).atualizar(produtoAtualizado);
        verify(repository).buscarPorCodigoBarras(codigoBarras);
    }

    @Test
    void deveLancarExcecaoQuandoCodigoBarrasForNulo() {
        // Arrange
        Produto produto = new Produto("123", "Nome", "Categoria", 10, 5.0, 10.0, "Fornecedor");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            useCase.executar(null, produto);
        });
    }

    @Test
    void deveLancarExcecaoQuandoCodigoBarrasForVazio() {
        // Arrange
        Produto produto = new Produto("123", "Nome", "Categoria", 10, 5.0, 10.0, "Fornecedor");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            useCase.executar("", produto);
        });
    }

    @Test
    void deveLancarExcecaoQuandoProdutoForNulo() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            useCase.executar("123456789", null);
        });
    }

    @Test
    void deveLancarExcecaoQuandoProdutoNaoExistir() {
        // Arrange
        String codigoBarras = "123456789";
        Produto produto = new Produto(codigoBarras, "Nome", "Categoria", 10, 5.0, 10.0, "Fornecedor");

        when(repository.existePorCodigoBarras(codigoBarras)).thenReturn(false);

        // Act & Assert
        assertThrows(ProdutoNaoEncontradoException.class, () -> {
            useCase.executar(codigoBarras, produto);
        });

        verify(repository).existePorCodigoBarras(codigoBarras);
        verify(repository, never()).atualizar(any());
    }

    @Test
    void deveLancarExcecaoQuandoNomeForNulo() {
        // Arrange
        String codigoBarras = "123456789";
        Produto produto = new Produto(codigoBarras, null, "Categoria", 10, 5.0, 10.0, "Fornecedor");

        when(repository.existePorCodigoBarras(codigoBarras)).thenReturn(true);

        // Act & Assert
        assertThrows(ValidacaoProdutoException.class, () -> {
            useCase.executar(codigoBarras, produto);
        });
    }

    @Test
    void deveLancarExcecaoQuandoNomeForVazio() {
        // Arrange
        String codigoBarras = "123456789";
        Produto produto = new Produto(codigoBarras, "", "Categoria", 10, 5.0, 10.0, "Fornecedor");

        when(repository.existePorCodigoBarras(codigoBarras)).thenReturn(true);

        // Act & Assert
        assertThrows(ValidacaoProdutoException.class, () -> {
            useCase.executar(codigoBarras, produto);
        });
    }

    @Test
    void deveLancarExcecaoQuandoCategoriaForNula() {
        // Arrange
        String codigoBarras = "123456789";
        Produto produto = new Produto(codigoBarras, "Nome", null, 10, 5.0, 10.0, "Fornecedor");

        when(repository.existePorCodigoBarras(codigoBarras)).thenReturn(true);

        // Act & Assert
        assertThrows(ValidacaoProdutoException.class, () -> {
            useCase.executar(codigoBarras, produto);
        });
    }

    @Test
    void deveLancarExcecaoQuandoFornecedorForNulo() {
        // Arrange
        String codigoBarras = "123456789";
        Produto produto = new Produto(codigoBarras, "Nome", "Categoria", 10, 5.0, 10.0, null);

        when(repository.existePorCodigoBarras(codigoBarras)).thenReturn(true);

        // Act & Assert
        assertThrows(ValidacaoProdutoException.class, () -> {
            useCase.executar(codigoBarras, produto);
        });
    }

    @Test
    void deveLancarExcecaoQuandoQuantidadeForNegativa() {
        // Arrange
        String codigoBarras = "123456789";
        Produto produto = new Produto(codigoBarras, "Nome", "Categoria", -1, 5.0, 10.0, "Fornecedor");

        when(repository.existePorCodigoBarras(codigoBarras)).thenReturn(true);

        // Act & Assert
        assertThrows(ValidacaoProdutoException.class, () -> {
            useCase.executar(codigoBarras, produto);
        });
    }

    @Test
    void deveLancarExcecaoQuandoPrecoCustoForNegativo() {
        // Arrange
        String codigoBarras = "123456789";
        Produto produto = new Produto(codigoBarras, "Nome", "Categoria", 10, -5.0, 10.0, "Fornecedor");

        when(repository.existePorCodigoBarras(codigoBarras)).thenReturn(true);

        // Act & Assert
        assertThrows(ValidacaoProdutoException.class, () -> {
            useCase.executar(codigoBarras, produto);
        });
    }

    @Test
    void deveLancarExcecaoQuandoPrecoVendaForNegativo() {
        // Arrange
        String codigoBarras = "123456789";
        Produto produto = new Produto(codigoBarras, "Nome", "Categoria", 10, 5.0, -10.0, "Fornecedor");

        when(repository.existePorCodigoBarras(codigoBarras)).thenReturn(true);

        // Act & Assert
        assertThrows(ValidacaoProdutoException.class, () -> {
            useCase.executar(codigoBarras, produto);
        });
    }
} 