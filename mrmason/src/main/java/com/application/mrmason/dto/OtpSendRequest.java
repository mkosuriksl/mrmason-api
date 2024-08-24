package com.application.mrmason.dto;

import com.application.mrmason.enums.RegSource;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OtpSendRequest {

	private String contactDetail;

	private RegSource regSource;
}
