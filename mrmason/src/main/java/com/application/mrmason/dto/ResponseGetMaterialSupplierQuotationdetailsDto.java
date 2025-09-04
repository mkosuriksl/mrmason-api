package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.MaterialSupplier;

import lombok.Data;

@Data
public class ResponseGetMaterialSupplierQuotationdetailsDto {
	private String message;
	private boolean status;
	private List<MaterialSupplier> materialSuppliers;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}