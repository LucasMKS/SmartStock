package com.lucasmks;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lucasmks.domain.exception.ProdutoNaoEncontradoException;
import com.lucasmks.domain.repository.ProdutoRepository;
import com.lucasmks.domain.usecase.DeletarProdutoUseCase;

@ExtendWith(MockitoExtension.class)
class DeletarProdutoUseCaseTest {

    @Mock
    private ProdutoRepository repository;

    private DeletarProdutoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new DeletarProdutoUseCase(repository);
    }

    @Test
    void deveDeletarProdutoComSucesso() {
        // Arrange
        String codigoBarras = "123456789";
        when(repository.existePorCodigoBarras(codigoBarras)).thenReturn(true);

        // Act
        useCase.executar(codigoBarras);

        // Assert
        verify(repository).existePorCodigoBarras(codigoBarras);
        verify(repository).remover(codigoBarras);
    }

    @Test
    void deveLancarExcecaoQuandoCodigoBarrasForNulo() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            useCase.executar(null);
        });
    }

    @Test
    void deveLancarExcecaoQuandoCodigoBarrasForVazio() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            useCase.executar("");
        });
    }

    @Test
    void deveLancarExcecaoQuandoProdutoNaoExistir() {
        // Arrange
        String codigoBarras = "123456789";
        when(repository.existePorCodigoBarras(codigoBarras)).thenReturn(false);

        // Act & Assert
        assertThrows(ProdutoNaoEncontradoException.class, () -> {
            useCase.executar(codigoBarras);
        });

        verify(repository).existePorCodigoBarras(codigoBarras);
        verify(repository, never()).remover(any());
    }
} 