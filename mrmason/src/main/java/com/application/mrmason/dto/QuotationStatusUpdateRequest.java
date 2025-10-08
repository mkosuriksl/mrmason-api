package com.application.mrmason.dto;

import com.application.mrmason.enums.Status;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuotationStatusUpdateRequest {
    private String quotationId;
    private String invoiceNumber;
    private Status status; // e.g. INVOICED, APPROVED, REJECTED, etc.
}

