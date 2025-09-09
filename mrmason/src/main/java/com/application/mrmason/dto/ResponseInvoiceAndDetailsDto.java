package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.Invoice;
import com.application.mrmason.entity.MaterialSupplier;
import com.application.mrmason.entity.MaterialSupplierQuotationHeader;

import lombok.Data;

@Data
public class ResponseInvoiceAndDetailsDto {

    private boolean status;
    private String message;
    private List<Invoice> invoices;
    private List<MaterialSupplier> materialSuppliers;
    private List<MaterialSupplierQuotationHeader> materialSupplierQuotationHeaders;
    private int invoiceCurrentPage;
    private int invoicePageSize;
    private long invoiceTotalElements;
    private int invoiceTotalPages;

//    private int supplierCurrentPage;
//    private int supplierPageSize;
//    private long supplierTotalElements;
//    private int supplierTotalPages;
}



