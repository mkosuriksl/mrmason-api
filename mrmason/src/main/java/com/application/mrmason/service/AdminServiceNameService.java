package com.application.mrmason.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.application.mrmason.dto.AdminServiceNameDto;
import com.application.mrmason.entity.AdminServiceName;

public interface AdminServiceNameService {
	AdminServiceNameDto addAdminServiceNameRequest(AdminServiceName amc);
//	List<AdminServiceName> getAdminServiceDetails(String serviceId,String serviceName,String serviceSubCat);
	public Page<AdminServiceName> getAdminServiceDetails(String serviceId, String serviceName, String serviceSubCat, Pageable pageable);
	AdminServiceNameDto updateAdminServiceDetails(AdminServiceName amc);
}
