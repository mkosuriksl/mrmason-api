package com.application.mrmason.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseMaterialRequirementByRequestDTO {
    private String message;
    private boolean status;
    private List<MaterialRequirementByRequestDTO> data;
}