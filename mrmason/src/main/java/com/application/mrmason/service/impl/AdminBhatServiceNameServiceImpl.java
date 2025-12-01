package com.application.mrmason.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.AdminBhatServiceNameDto;
import com.application.mrmason.entity.AdminBhatServiceName;
import com.application.mrmason.repository.AdminBhatServiceNameRepo;
import com.application.mrmason.service.AdminBhatServiceNameService;

@Service
public class AdminBhatServiceNameServiceImpl implements AdminBhatServiceNameService {

    @Autowired
    AdminBhatServiceNameRepo serviceRepo;

    @Override
    public AdminBhatServiceNameDto addAdminBhatServiceNameRequest(AdminBhatServiceName service) {
        if (serviceRepo.findByServiceId(service.getServiceId()) == null) {
            serviceRepo.save(service);
            return getServiceById(service.getServiceId());
        }
        return null;

    }

    @Override
    public Page<AdminBhatServiceName> getAdminBhatServiceDetails(String serviceId, String serviceName,
            String serviceSubCat, Pageable pageable) {
        if (serviceId != null || serviceName != null || serviceSubCat != null) {
            return serviceRepo.findByServiceIdOrServiceNameOrServiceSubCategoryOrderByAddedDateDesc(serviceId,
                    serviceName, serviceSubCat, pageable);
        } else {
            return Page.empty();
        }
    }

    @Override
    public AdminBhatServiceNameDto updateAdminBhatServiceDetails(AdminBhatServiceName service) {
        String id = service.getServiceId();
        String addedBy = service.getAddedBy();
        String serviceName = service.getServiceName();
        String subCategory = service.getServiceSubCategory();

        Optional<AdminBhatServiceName> adminService = Optional.ofNullable(serviceRepo.findByServiceId(id));
        if (adminService.isPresent()) {
            adminService.get().setAddedBy(addedBy);
            adminService.get().setServiceName(serviceName);
            adminService.get().setServiceSubCategory(subCategory);

            serviceRepo.save(adminService.get());
            return getServiceById(id);
        }
        return null;
    }

    public AdminBhatServiceNameDto getServiceById(String id) {
        if (serviceRepo.findByServiceId(id) != null) {
            Optional<AdminBhatServiceName> serviceCat = Optional.ofNullable(serviceRepo.findByServiceId(id));
            AdminBhatServiceName serviceCatData = serviceCat.get();
            AdminBhatServiceNameDto serviceDto = new AdminBhatServiceNameDto();

            serviceDto.setServiceId(serviceCatData.getServiceId());
            serviceDto.setServiceSubCategory(serviceCatData.getServiceSubCategory());
            serviceDto.setAddedBy(serviceCatData.getAddedBy());
            serviceDto.setAddedDate(serviceCatData.getAddedDate());
            serviceDto.setServiceName(serviceCatData.getServiceName());

            return serviceDto;

        }
        return null;
    }

}
