package com.application.mrmason.dto;

import java.time.LocalDate;
import java.util.List;

import com.application.mrmason.entity.MaterialSupplier;
import com.application.mrmason.enums.Status;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaterialSupplierQuotations {
	private String cmatRequestId;
	private String invoiceNumber;
	private Status invoiceStatus;
	private Status quotationStatus;
	private LocalDate invoiceDate;
    private List<MaterialSupplier> quotations;
}
