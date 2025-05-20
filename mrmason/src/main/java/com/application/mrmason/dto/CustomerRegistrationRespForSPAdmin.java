package com.application.mrmason.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerRegistrationRespForSPAdmin {
	private Long id;
	private String userid;
	private String userEmail;
	private String userMobile;
	private String userType;
	private String userTown;
	private String userDistrict;
	private String userState;
	private String userPincode;
	private String regDate;
	private String updatedBy;
	private String updatedDate;
	private String userName;
	public CustomerRegistrationRespForSPAdmin(Long id, String userid, String userEmail, String userMobile,
			String userType, String userTown, String userDistrict, String userState, String userPincode, String regDate,
			String updatedBy, String updatedDate, String userName) {
		super();
		this.id = id;
		this.userid = userid;
		this.userEmail = userEmail;
		this.userMobile = userMobile;
		this.userType = userType;
		this.userTown = userTown;
		this.userDistrict = userDistrict;
		this.userState = userState;
		this.userPincode = userPincode;
		this.regDate = regDate;
		this.updatedBy = updatedBy;
		this.updatedDate = updatedDate;
		this.userName = userName;
	}
	public CustomerRegistrationRespForSPAdmin() {}
	
}
