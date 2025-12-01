package com.application.mrmason.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.application.mrmason.dto.AdminBhatServiceNameDto;
import com.application.mrmason.entity.AdminBhatServiceName;

public interface AdminBhatServiceNameService {

    AdminBhatServiceNameDto addAdminBhatServiceNameRequest(AdminBhatServiceName amc);

    public Page<AdminBhatServiceName> getAdminBhatServiceDetails(String serviceId, String serviceName,
            String serviceSubCat, Pageable pageable);

    AdminBhatServiceNameDto updateAdminBhatServiceDetails(AdminBhatServiceName amc);

}
