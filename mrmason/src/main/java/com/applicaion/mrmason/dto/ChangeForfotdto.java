package com.applicaion.mrmason.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeForfotdto {

	String email;
	String otp;
	String newPassword;
	String confirmPassword;
	String oldPassword;
}
