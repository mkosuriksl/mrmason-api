package com.application.mrmason.service;

import java.util.List;

import com.application.mrmason.entity.UserServiceCharges;

public interface UserServiceChargesService {

	public List<UserServiceCharges> addCharges(List<UserServiceCharges> chargesList);
	public UserServiceCharges updateCharges(UserServiceCharges charges);
	public List<UserServiceCharges> getUserServiceCharges(String serviceChargeKey, String serviceId, String location,
			String brand, String model,String updatedBy,String subcategory) ;
}
