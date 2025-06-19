package com.application.mrmason.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequest {
	
	public String bodSeqNo;
	private String name;
	private String location;
	private String state;
	private String district;
	private String address;
	private String city;
	private String linkedInURL;
	private String highestQualification;
}
