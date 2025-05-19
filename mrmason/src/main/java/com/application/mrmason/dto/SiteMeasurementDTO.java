package com.application.mrmason.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SiteMeasurementDTO {
	private String serviceRequestId;
	private String eastSiteLength;
	private String westSiteLength;
	private String southSiteLength;
	private String northSiteLength;
	private String location;
	private String expectedBedRooms;
	private String expectedAttachedBathRooms;
	private String expectedAdditionalBathRooms;
	private Date expectedStartDate;
	private Date updatedDate;
	private String updatedBy;
	private String customerId;
	private String userId;
	private String buildingType;
	private String noOfFloors;
	private Date requestDate;
	private String status;

}