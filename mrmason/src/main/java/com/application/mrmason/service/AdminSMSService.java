package com.application.mrmason.service;

import java.util.List;

import com.application.mrmason.dto.ResponseAdminMailDto;
import com.application.mrmason.entity.AdminSms;

public interface AdminSMSService {
	
	ResponseAdminMailDto addApiRequest(AdminSms admin);
	ResponseAdminMailDto updateApiRequest(AdminSms admin);
	List<AdminSms> getAllSMSDetails();


}
