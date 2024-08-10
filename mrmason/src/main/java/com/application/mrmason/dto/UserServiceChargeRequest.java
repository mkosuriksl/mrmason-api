package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.UserServiceCharges;

import lombok.Data;

@Data
public class UserServiceChargeRequest {

	private List<UserServiceCharges> chargesList;
	private String subCategory;

}
