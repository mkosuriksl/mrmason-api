package com.application.mrmason.dto;

import lombok.Data;

@Data
public class GenericResponse<T> {
    private String message;
    private boolean success;
    private T data;

    public GenericResponse(String message, boolean success, T data) {
        this.message = message;
        this.success = success;
        this.data = data;
    }

    // Getters and setters
}
