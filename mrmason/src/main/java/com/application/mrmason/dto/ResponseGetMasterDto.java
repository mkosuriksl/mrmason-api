package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.MaterialMaster;
import com.application.mrmason.entity.MaterialPricing;

import lombok.Data;

@Data
public class ResponseGetMasterDto {
	private String message;
	private boolean status;
	private List<MaterialMaster> materialMasters;
	private List<MaterialSupplierDto> materialSupplier;
	private List<MaterialPricing> masterPricing;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}
