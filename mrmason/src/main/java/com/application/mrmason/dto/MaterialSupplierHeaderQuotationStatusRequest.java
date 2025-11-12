package com.application.mrmason.dto;

import com.application.mrmason.enums.Status;

import lombok.Data;

@Data
public class MaterialSupplierHeaderQuotationStatusRequest {
	private String cmatRequestId;
	private Status invoiceStatus;
	private Status quotationStatus;
}
