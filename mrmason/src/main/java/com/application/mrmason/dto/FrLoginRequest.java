package com.application.mrmason.dto;

import com.application.mrmason.enums.RegSource;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FrLoginRequest {
	private String email;
	private String mobile;
	private String password;
	private RegSource regSource;
}
