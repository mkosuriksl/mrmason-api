package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.SiteMeasurement;

import lombok.Data;

@Data
public class ResponseGetSiteMeasurementDTO {

	private String message;
	private boolean status;
	private List<SiteMeasurement> data;
}
