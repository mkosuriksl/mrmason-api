package com.application.mrmason.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseAdminMailDto {
	private String message;
	private boolean status;
	private Object data;
}
