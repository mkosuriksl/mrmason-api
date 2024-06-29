package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.SpServiceDetails;
import com.application.mrmason.entity.User;

import lombok.Data;

@Data
public class ResponseUserUserServicesDto {

	private String message;
	private boolean status;
	private List<User>  userData;
	List<SpServiceDetails> userServicesData;
}
