package com.application.mrmason.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResponseModel {

	public ResponseModel() {
		// TODO Auto-generated constructor stub
	}
	private String error;
	private String msg;

}
