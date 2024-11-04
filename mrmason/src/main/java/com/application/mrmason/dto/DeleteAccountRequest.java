package com.application.mrmason.dto;

import lombok.Data;

@Data
public class DeleteAccountRequest {
	private String spId;
	private String reason;

}
