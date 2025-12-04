package com.application.mrmason.dto;
import java.time.LocalDate;
import java.util.List;

import com.application.mrmason.enums.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServiceRequestHeaderDTO {

    private String requestId;
    private LocalDate requestDate;
    private String requestedBy;
    private String contactNumber;
    private String vehicleId;
    private String brand;
    private String model;
    private Status status;
    private String updatedBy;
    private LocalDate updatedDate;

    private List<ServiceRequestDetailDTO> serviceRequestDetails;
}