package com.application.mrmason.service;

import com.application.mrmason.dto.AdminDetailsDto;
import com.application.mrmason.entity.AdminDetails;

public interface AdminDetailsService {
	AdminDetails registerDetails(AdminDetails admin);
	AdminDetailsDto getDetails(String email, String phno);
	AdminDetailsDto getAdminDetails(AdminDetailsDto admin);
	String updateAdminData(AdminDetails admin);
	String adminLoginDetails(String userEmail, String phno, String userPassword);
}
