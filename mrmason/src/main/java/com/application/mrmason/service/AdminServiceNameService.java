package com.application.mrmason.service;

import java.util.List;

import com.application.mrmason.dto.AdminServiceNameDto;
import com.application.mrmason.entity.AdminServiceName;

public interface AdminServiceNameService {
	AdminServiceNameDto addAdminServiceNameRequest(AdminServiceName amc);
	List<AdminServiceName> getAdminServiceDetails(AdminServiceName amc);
	AdminServiceNameDto updateAdminServiceDetails(AdminServiceName amc);
}
