package com.application.mrmason.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceRequestWithCustomerDTO {
    private Long reqSeqId;
    private String serviceName;
    private String requestId;
    private String serviceRequestDate;
    private String requestedBy;
    private String location;
    private String description;
    private String status;
    private String serviceDateDb;
    private String assetId;

    // From CustomerRegistration
    private String userEmail;
    private String userName;
    private String userMobile;
    private String userDistrict;
    private String userState;
    private String userPincode;
}

