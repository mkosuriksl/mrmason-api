package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.ServicePersonRentalEntity;

import lombok.Data;

@Data
public class ResponseListSPRentalDTO {
	private String message;
	private boolean status;
	private List<ServicePersonRentalEntity> data;

}
