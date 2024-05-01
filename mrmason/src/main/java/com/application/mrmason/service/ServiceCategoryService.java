package com.application.mrmason.service;

import java.util.List;

import com.application.mrmason.dto.ServiceCategoryDto;
import com.application.mrmason.entity.ServiceCategory;
import com.application.mrmason.entity.ServiceCategoryMech;

public interface ServiceCategoryService {
	ServiceCategoryDto addServiceCategory(ServiceCategory service);
	List<ServiceCategory> getServiceCategory(ServiceCategory service);
	ServiceCategoryDto updateServiceCategory(ServiceCategory service);
	List<ServiceCategoryMech> getMechServiceCategory(ServiceCategory service);
	ServiceCategoryDto getServiceById(String id);
	ServiceCategoryDto getMechServiceById(String id);
}