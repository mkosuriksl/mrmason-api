package com.application.mrmason.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.application.mrmason.dto.ServiceCategoryDto;
import com.application.mrmason.entity.ServiceCategory;
import com.application.mrmason.repository.ServiceCategoryRepo;
import com.application.mrmason.service.ServiceCategoryService;

import jakarta.transaction.Transactional;
@Service
public class ServiceCategoryServiceImpl implements ServiceCategoryService{
    @Autowired
	ServiceCategoryRepo serviceRepo;
	
	@Override
	public ServiceCategoryDto addServiceCategory(ServiceCategory service) {
		serviceRepo.save(service);
		return getServiceById(service.getId());
		
	}

	@Override
	public List<ServiceCategory> getServiceCategory(ServiceCategory service) {
		Long id=service.getId();
		String category=service.getServiceCategory();
		
        if( id !=null && category==null ) {
			Optional<List<ServiceCategory>> user=Optional.of((serviceRepo.findByIdOrderByCreateDateDesc(id.longValue())));
			return user.get();
		}else {
			List<ServiceCategory> user=(serviceRepo.findByServiceCategoryOrderByCreateDateDesc(category));
			return user;
		}
	}

	@Override
	public ServiceCategoryDto updateServiceCategory(ServiceCategory service) {
		long id=service.getId();
		String updatedBy=service.getUpdatedBy();
		String category=service.getServiceCategory();
		String subCategory=service.getServiceSubCategory();
		
		Optional<ServiceCategory>  serviceCategory=serviceRepo.findById(id);
		if(serviceCategory.isPresent()) {
			serviceCategory.get().setUpdatedBy(updatedBy);;
			serviceCategory.get().setServiceCategory(category);;
			serviceCategory.get().setServiceSubCategory(subCategory);
			
			serviceRepo.save(serviceCategory.get());
			return getServiceById(id);
		}
		return null;
	}
	
	@Override
	public ServiceCategoryDto getServiceById(long id) {
		if (serviceRepo.findById(id) != null) {
			Optional<ServiceCategory> serviceCat = serviceRepo.findById(id);
			ServiceCategory serviceCatData = serviceCat.get();
			ServiceCategoryDto serviceDto = new ServiceCategoryDto();

			serviceDto.setId(serviceCatData.getId());
			serviceDto.setServiceCategory(serviceCatData.getServiceCategory());
			serviceDto.setServiceSubCategory(serviceCatData.getServiceSubCategory());
			serviceDto.setUpdatedBy(serviceCatData.getUpdatedBy());
			serviceDto.setUpdatedDate(serviceCatData.getUpdatedDate());
			serviceDto.setCreateDate(serviceCatData.getCreateDate());
			serviceDto.setAddedBy(serviceCatData.getAddedBy());
		
			return serviceDto;

		}
		return null;
	}

	
 
}
