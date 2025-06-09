package com.application.mrmason.dto;

import java.util.List;

import lombok.Data;
@Data
public class ResponseListUserDto {
	private String message;
	private boolean status;
	private List<UserResponseDTO> data;
}
