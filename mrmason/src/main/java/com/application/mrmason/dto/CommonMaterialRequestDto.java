package com.application.mrmason.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class CommonMaterialRequestDto {
    private String materialCategory;
    private String requestedBy;
    private LocalDate deliveryDate;
    private String deliveryLocation;
    private List<CMaterialReqHeaderDetailsDTO> materialRequests;
}
