package com.application.mrmason.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.application.mrmason.entity.AdminServiceCharges;

public interface AdminServiceChargesService {

	public List<AdminServiceCharges> addCharges(List<AdminServiceCharges> chargesList);
//	public AdminServiceCharges addCharges(AdminServiceCharges charges);
	public AdminServiceCharges updateCharges(AdminServiceCharges charges);
//	public List<AdminServiceCharges> getAdminServiceCharges(String serviceChargeKey, String serviceId, String location,
//			String brand, String model,String updatedBy,String subcategory) ;
	public Page<AdminServiceCharges> getAdminServiceCharges(String serviceChargeKey, String serviceId, String location,
			String brand, String model,String updatedBy,String subcategory,Pageable pageable);
}
