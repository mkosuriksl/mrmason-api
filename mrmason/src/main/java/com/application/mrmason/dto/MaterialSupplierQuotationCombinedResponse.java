package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.MaterialSupplierQuotationHeader;
import com.application.mrmason.entity.MaterialSupplierQuotationHeaderHistory;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaterialSupplierQuotationCombinedResponse {
	private String message;
	private boolean status;
	private List<MaterialSupplierQuotationHeader> materialSupplierQuotationHeaders;
	private List<MaterialSupplierQuotationHeaderHistory> materialSupplierQuotationHeadersHistory;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
}
