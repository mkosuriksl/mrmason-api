package com.application.mrmason.service;

import java.util.List;

import com.application.mrmason.dto.ResponseAdminMailDto;
import com.application.mrmason.entity.AdminMail;

public interface AdminMailService {
	
	ResponseAdminMailDto addApiRequest(AdminMail admin);
	ResponseAdminMailDto getApiRequest(String email);
	ResponseAdminMailDto updateApiRequest(AdminMail admin);
	List<AdminMail> getAllEmailDetails();


}
