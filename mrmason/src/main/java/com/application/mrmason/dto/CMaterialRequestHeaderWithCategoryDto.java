package com.application.mrmason.dto;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CMaterialRequestHeaderWithCategoryDto {
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
    private String materialCategory;   // <-- new field
}

