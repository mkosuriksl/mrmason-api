package com.application.mrmason.service;

import java.util.List;

import com.application.mrmason.dto.ServiceCategoryDto;
import com.application.mrmason.entity.ServiceCategory;

public interface ServiceCategoryService {
	ServiceCategoryDto addServiceCategory(ServiceCategory service);
	List<ServiceCategory> getServiceCategory(ServiceCategory service);
	ServiceCategoryDto updateServiceCategory(ServiceCategory service);
	ServiceCategoryDto getServiceById(String id);
	List<ServiceCategory> getServiceCategoryCivil(String category);
	List<ServiceCategory> getServiceCategoryNonCivil(String category);
	
}