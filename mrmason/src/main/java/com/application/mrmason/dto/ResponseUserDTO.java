package com.application.mrmason.dto;

import lombok.Data;

@Data
public class ResponseUserDTO {
	private String message;
	private boolean status;
	private Userdto userData;

}
