package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.MaterialSupplier;
import com.application.mrmason.entity.MaterialSupplierQuotationHeader;

import lombok.Data;

@Data
public class ResponseGetMaterialSupplierQuotationsheaderDto {
	private String message;
	private boolean status;
	private List<MaterialSupplierQuotationHeader> materialSupplierQuotationHeaders;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}