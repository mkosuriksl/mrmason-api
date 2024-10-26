package com.application.mrmason.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseAdminUiEndPointDto<T> {

    private String message;
    private boolean success;
    private T data;

    public ResponseAdminUiEndPointDto(String message, boolean success, T data) {
        this.message = message;
        this.success = success;
        this.data = data;
    }

}
