package com.application.mrmason.dto;
import java.util.List;

import com.application.mrmason.entity.MaterialSupplierAssets;

import lombok.Data;

@Data
public class ResponseGetAssetsDto {
    private String message;
    private boolean status;
    private List<MaterialSupplierAssets> assets;
    private List<MaterialSupplierDto> suppliers;
    private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
