package com.application.mrmason.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseMaterialRequirementByRequestDTO {
    private String message;
    private boolean status;
    private MaterialRequirementByRequestDTO data;
}