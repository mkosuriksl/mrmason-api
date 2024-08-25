package com.application.mrmason.dto;

import com.application.mrmason.enums.RegSource;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequest {
	private String email;
	private String password;
	private String mobile;
	private RegSource regSource;
}
