package com.application.mrmason.dto;

import java.util.List;
import com.application.mrmason.entity.MaterialPricing;
import lombok.Data;

@Data
public class ResponseGetMaterialPricingDto {
    private String message;
    private boolean status;
    private List<MaterialPricing> materialPricings;
    private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}

