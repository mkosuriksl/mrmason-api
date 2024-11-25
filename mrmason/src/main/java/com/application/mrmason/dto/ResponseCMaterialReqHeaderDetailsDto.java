package com.application.mrmason.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponseCMaterialReqHeaderDetailsDto {

    private boolean status;
    private String message;
    private List<CMaterialReqHeaderDetailsResponseDTO> materialRequestDetailsList;
}
