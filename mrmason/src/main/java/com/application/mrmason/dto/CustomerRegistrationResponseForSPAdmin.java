package com.application.mrmason.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CustomerRegistrationResponseForSPAdmin {
	private String message;
	private String status;
	private CustomerRegistrationRespForSPAdmin data;
}
