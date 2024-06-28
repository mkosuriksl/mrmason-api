package com.application.mrmason.dto;

import com.application.mrmason.entity.AdminServiceCharges;

import lombok.Data;

@Data
public class ResponseAdminServiceChargesDto {

	private String message;
	private boolean status;
	private AdminServiceCharges serviceChargeData;
}
