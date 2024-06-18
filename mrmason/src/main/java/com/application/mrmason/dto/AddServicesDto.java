package com.application.mrmason.dto;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<String> serviceNameList;
}
