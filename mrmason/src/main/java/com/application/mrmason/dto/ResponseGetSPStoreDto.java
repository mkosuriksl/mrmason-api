package com.application.mrmason.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseGetSPStoreDto<T> {
    private String message;
    private boolean status;
    private List<T> data;

}
