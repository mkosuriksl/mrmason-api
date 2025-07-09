package com.application.mrmason.service;

import org.springframework.data.domain.Page;

import com.application.mrmason.dto.AdminDetailsDto;
import com.application.mrmason.dto.ResponceAdminDetailsDto;
import com.application.mrmason.entity.AdminAsset;
import com.application.mrmason.entity.AdminDetails;
import com.application.mrmason.enums.RegSource;

public interface AdminDetailsService {
	AdminDetails registerDetails(AdminDetails admin);

	AdminDetailsDto getDetails(String email, String phno);

//	AdminDetailsDto getAdminDetails(String email,String mobile);
	
	public Page<AdminAsset> getAdminDetails(String email, String mobile, int pageNo, int pageSize);

	String updateAdminData(AdminDetails admin);

	ResponceAdminDetailsDto adminLoginDetails(String userEmail, String phno, String userPassword);

	String changePassword(String usermail, String oldPass, String newPass, String confPass, String phno);

	String forgetPassword(String mobile,String email, String otp, String newPass, String confPass);

//	String sendMail(String email,RegSource regSource);
	
	String sendMail(String email);
	
	String sendSms(String mobile,RegSource regSource);
}
