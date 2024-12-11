package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.ServicePersonStoreDetailsEntity;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseSPStoreDto {

	private String message;
	private boolean status;
	private ServicePersonStoreDetailsEntity data;
	private List<ServicePersonStoreDetailsEntity> storesList;

}
