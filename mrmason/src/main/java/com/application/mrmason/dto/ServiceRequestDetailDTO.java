package com.application.mrmason.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ServiceRequestDetailDTO {
	private String serviceLineId;
	private String requestId;
	private String serviceId;
	private double serviceCharge;
	private int gst;
	private int discount;
	private String updatedBy;
	private LocalDate updatedDate;
}