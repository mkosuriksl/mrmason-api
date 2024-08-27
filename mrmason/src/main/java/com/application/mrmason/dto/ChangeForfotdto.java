package com.application.mrmason.dto;

import com.application.mrmason.enums.RegSource;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeForfotdto {

	String email;
	String mobile;
	String otp;
	String newPassword;
	String confirmPassword;
	String oldPassword;
	RegSource regSource;
}
