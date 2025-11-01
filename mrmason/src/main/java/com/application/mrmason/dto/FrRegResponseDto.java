package com.application.mrmason.dto;

import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FrRegResponseDto {

	private String frUserid;
	private String frEmail;
	private String frMobile;
	private String frLinkedInProfile;
	private String emailVerified;
	private String mobileVerified;
	private RegSource regSource;
	private UserType userType;
	private String status;
	private String updatedDate;
}
