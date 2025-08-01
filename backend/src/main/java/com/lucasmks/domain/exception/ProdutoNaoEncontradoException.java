package com.lucasmks.domain.exception;

public class ProdutoNaoEncontradoException extends RuntimeException {
    public ProdutoNaoEncontradoException(String message) {
        super(message);
    }
}