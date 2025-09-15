package com.application.mrmason.dto;

import com.application.mrmason.enums.RegSource;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordDto {
	private String email;
	private String mobile;
	private String oldPass;
	private String newPass;
	private String confPass;
	private String otp;
	private RegSource regSource;
}
