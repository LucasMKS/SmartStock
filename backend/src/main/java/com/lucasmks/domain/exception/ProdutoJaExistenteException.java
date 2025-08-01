package com.lucasmks.domain.exception;

public class ProdutoJaExistenteException extends RuntimeException {
    public ProdutoJaExistenteException(String message) {
        super(message);
    }
}