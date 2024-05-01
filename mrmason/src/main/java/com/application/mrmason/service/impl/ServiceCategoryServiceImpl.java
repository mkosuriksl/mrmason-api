package com.application.mrmason.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.ServiceCategoryDto;
import com.application.mrmason.entity.ServiceCategory;
import com.application.mrmason.entity.ServiceCategoryMech;
import com.application.mrmason.repository.ServiceCategoryMechRepo;
import com.application.mrmason.repository.ServiceCategoryRepo;
import com.application.mrmason.service.ServiceCategoryService;

@Service
public class ServiceCategoryServiceImpl implements ServiceCategoryService {
	@Autowired
	ServiceCategoryRepo serviceRepo;
	@Autowired
	ServiceCategoryMechRepo mechRepo;

	@Override
	public ServiceCategoryDto addServiceCategory(ServiceCategory service) {
		String serviceCat = service.getServiceCategory();
		String serviceSubCat = service.getServiceSubCategory();
		if (serviceRepo.findByServiceCategoryAndServiceSubCategory(serviceCat, serviceSubCat) == null
				&& mechRepo.findByServiceCategoryAndServiceSubCategory(serviceCat, serviceSubCat) == null) {
			if (service.getServiceCategory().equalsIgnoreCase("civil")) {
				ServiceCategory data = serviceRepo.save(service);
				return getServiceById(data.getId());
			} else {
				ServiceCategoryMech mech = new ServiceCategoryMech();
				mech.setAddedBy(service.getAddedBy());
				mech.setServiceCategory(serviceCat);
				mech.setServiceSubCategory(serviceSubCat);
				ServiceCategoryMech mechData = mechRepo.save(mech);
				return getMechServiceById(mechData.getId());
			}
		} else {
			return null;
		}

	}

	@Override
	public List<ServiceCategory> getServiceCategory(ServiceCategory service) {
		String id = service.getId();
		String category = service.getServiceCategory();

		if (id != null && category == null) {
			Optional<List<ServiceCategory>> user = Optional.of((serviceRepo.findByIdOrderByCreateDateDesc(id)));
			return user.get();
		} else {
			List<ServiceCategory> user = (serviceRepo.findByServiceCategoryOrderByCreateDateDesc(category));
			return user;
		}
	}
	
	@Override
	public List<ServiceCategoryMech> getMechServiceCategory(ServiceCategory service) {
		String id = service.getId();
		String category = service.getServiceCategory();

		if (id != null && category == null) {
			Optional<List<ServiceCategoryMech>> user = Optional.of((mechRepo.findByIdOrderByCreateDateDesc(id)));
			return user.get();
		} else {
			List<ServiceCategoryMech> user = (mechRepo.findByServiceCategoryOrderByCreateDateDesc(category));
			return user;
		}
	}

	@Override
	public ServiceCategoryDto updateServiceCategory(ServiceCategory service) {
		String id = service.getId();
		String updatedBy = service.getUpdatedBy();
		String category = service.getServiceCategory();
		String subCategory = service.getServiceSubCategory();

		Optional<ServiceCategory> serviceCategory = serviceRepo.findById(id);
		Optional<ServiceCategoryMech> serviceCategoryMech = mechRepo.findById(id);
		if (serviceCategory.isPresent()&& !serviceCategoryMech.isPresent()) {
			serviceCategory.get().setUpdatedBy(updatedBy);
			serviceCategory.get().setServiceCategory(category);
			serviceCategory.get().setServiceSubCategory(subCategory);

			serviceRepo.save(serviceCategory.get());
			return getServiceById(id);
		}else if(!serviceCategory.isPresent()&& serviceCategoryMech.isPresent()) {
			serviceCategoryMech.get().setUpdatedBy(updatedBy);
			serviceCategoryMech.get().setServiceCategory(category);
			serviceCategoryMech.get().setServiceSubCategory(subCategory);

			mechRepo.save(serviceCategoryMech.get());
			return getMechServiceById(id);
		}
		return null;
	}

	@Override
	public ServiceCategoryDto getServiceById(String id) {
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

	@Override
	public ServiceCategoryDto getMechServiceById(String id) {

		Optional<ServiceCategoryMech> serviceCat = mechRepo.findById(id);
		ServiceCategoryMech serviceCatData = serviceCat.get();
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

}
