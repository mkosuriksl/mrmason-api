package com.application.mrmason.dto;

import com.application.mrmason.entity.UserType;

import lombok.Data;

@Data
public class CustomerResponseDTO {
	private long id;
	private String userid;
	private String userEmail;
	private String userMobile;
	private UserType userType;
	private String userName;
	private String userTown;
	private String userDistrict;
	private String userState;
	private String userPincode;
	private String regDate;
}

