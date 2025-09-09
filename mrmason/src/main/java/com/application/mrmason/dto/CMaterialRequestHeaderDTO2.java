package com.application.mrmason.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class CMaterialRequestHeaderDTO2 {
	private String materialRequestId;
    private int totalQty;
    private String customerEmail;
    private String customerName;
    private LocalDate createdDate;
    private String updatedBy;
    private LocalDate updatedDate;
    private String quoteId;
    private String requestedBy;
    private String customerMobile;
    private LocalDate deliveryDate;
    private String deliveryLocation;
    private List<CMaterialReqHeaderDetailsDTO2> cMaterialReqHeaderDetailsEntity;

    public void setCMaterialReqHeaderDetailsEntity(List<CMaterialReqHeaderDetailsDTO2> detailDtos) {
        this.cMaterialReqHeaderDetailsEntity = detailDtos;
    }

}
