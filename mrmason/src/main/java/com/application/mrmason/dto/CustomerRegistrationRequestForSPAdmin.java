package com.application.mrmason.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerRegistrationRequestForSPAdmin {
	private String userEmail;
	private String userMobile;
	private String userName;
	private String userTown;
	private String userDistrict;
	private String userState;
	private String userPincode;
	private String userId;
}
