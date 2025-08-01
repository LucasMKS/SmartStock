package com.lucasmks.domain.exception;

public class EstoqueInsuficienteException extends RuntimeException {
    public EstoqueInsuficienteException(String message) {
        super(message);
    }
}