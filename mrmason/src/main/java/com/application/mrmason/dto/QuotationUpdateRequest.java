package com.application.mrmason.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuotationUpdateRequest {

    private String cmatRequestId;
    private String status;
    private BigDecimal quotedAmount;
    private String updatedBy;

    private List<QuotationDetail> quotations;

    @Getter
    @Setter
    public static class QuotationDetail {
        private String materialLineItem;
        private BigDecimal discount;
        private double gst;
        private BigDecimal mrp;
    }
}

