package com.lucasmks.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ApiResponse<T> {
    
    @JsonProperty("success")
    private boolean success;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("data")
    private T data;
    
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    // Construtores
    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }
    
    public ApiResponse(boolean success, String message, T data) {
        this();
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Métodos estáticos para facilitar criação
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Operação realizada com sucesso", data);
    }
    
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}
