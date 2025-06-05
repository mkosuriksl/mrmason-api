package com.application.mrmason.entity;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

import com.application.mrmason.dto.ServiceRequestItem;

@Getter
@Setter
public class ServiceRequestPaintQuotationWrapper {
    private String requestId;
    private String serviceCATEGORY;
    private List<ServiceRequestItem> items;
}


