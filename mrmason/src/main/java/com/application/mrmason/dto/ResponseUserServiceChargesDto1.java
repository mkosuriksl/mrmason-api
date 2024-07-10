package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.UserServiceCharges;

import lombok.Data;
@Data
public class ResponseUserServiceChargesDto1 {
	
	private String message;
	private boolean status;
	private List<UserServiceCharges> getData;
}

