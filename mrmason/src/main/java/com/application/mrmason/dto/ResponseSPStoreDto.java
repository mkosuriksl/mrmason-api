package com.application.mrmason.dto;

import com.application.mrmason.entity.ServicePersonStoreDetailsEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseSPStoreDto {

	private String message;
	private boolean status;
	private ServicePersonStoreDetailsEntity data;

}
