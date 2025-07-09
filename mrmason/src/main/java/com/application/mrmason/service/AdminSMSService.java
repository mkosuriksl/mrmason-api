package com.application.mrmason.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.application.mrmason.dto.ResponseAdminMailDto;
import com.application.mrmason.entity.AdminSms;

public interface AdminSMSService {
	
	ResponseAdminMailDto addApiRequest(AdminSms admin);
	ResponseAdminMailDto updateApiRequest(AdminSms admin);
//	List<AdminSms> getAllSMSDetails();
	public Page<AdminSms> getAllSMSDetails(Pageable pageable) ;

}
