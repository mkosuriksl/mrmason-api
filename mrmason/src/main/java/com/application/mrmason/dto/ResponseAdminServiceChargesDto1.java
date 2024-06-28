package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.AdminServiceCharges;

import lombok.Data;

@Data
public class ResponseAdminServiceChargesDto1 {

	private String message;
	private boolean status;
	private List<AdminServiceCharges> getData;
}
