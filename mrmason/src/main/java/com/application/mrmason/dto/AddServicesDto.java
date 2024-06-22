package com.application.mrmason.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class AddServicesDto {
    private String userIdServiceId;
    private String serviceId;
    private String serviceSubCategory;
    private String status;
    private String bodSeqNo;
    private String updatedBy;
    private LocalDateTime updatedDate;
    private String updateDateFormat;
    private List<String> serviceIdList;  
    private String serviceIdServiceName;
    public String getServiceIdServiceName() {
        return serviceIdServiceName;
    }
    public void setServiceIdServiceName(String serviceIdServiceName) {
        this.serviceIdServiceName = serviceIdServiceName;
    }
    
    
    private String userServicesId;
    
}
