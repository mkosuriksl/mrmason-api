package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.ServiceRequestPaintQuotation;
import com.application.mrmason.entity.ServiceRequestQuotation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuotationFullResponseDTO {
    private QuotationWorkOrderResponseDTO workOrder;
    private ServiceRequestQuotation headerQuotation;
    private List<ServiceRequestPaintQuotation> paintQuotations;
}

