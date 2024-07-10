package com.application.mrmason.dto;

import com.application.mrmason.entity.UserServiceCharges;

import lombok.Data;

@Data
public class ResponseUserServiceChargesDto2 {

	private String message;
	private boolean status;
	private UserServiceCharges updatedData;
}
