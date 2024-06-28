package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.ServiceRequest;

import lombok.Data;

@Data
public class ResponseListServiceRequestDto1 {

	private String message;
	private boolean status;
	private ServiceRequest data;
}
