package com.application.mrmason.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.ServiceCategoryBhatDto;
import com.application.mrmason.entity.ServiceCategoryBhat;
import com.application.mrmason.repository.ServiceCategoryBhatRepo;
import com.application.mrmason.service.ServiceCategoryBhatService;

@Service
public class ServiceCategoryBhatServiceImpl implements ServiceCategoryBhatService {

    @Autowired
    ServiceCategoryBhatRepo serviceRepo;

    @Override
    public ServiceCategoryBhatDto addServiceCategory(ServiceCategoryBhat service) {
        String serviceCat = service.getServiceCategory();
        String serviceSubCat = service.getServiceSubCategory();
        if (serviceRepo.findByServiceCategoryAndServiceSubCategory(serviceCat, serviceSubCat) == null) {
            ServiceCategoryBhat data = serviceRepo.save(service);
            return getServiceById(data.getId());

        } else {
            return null;
        }

    }

    @Override
    public List<ServiceCategoryBhat> getServiceCategory(String id, String category, String subCat) {

        if (id != null) {
            Optional<List<ServiceCategoryBhat>> user = Optional
                    .ofNullable((serviceRepo.findByIdOrderByCreateDateDesc(id)));
            return user.get();
        } else {
            List<ServiceCategoryBhat> user = (serviceRepo.findByServiceCategoryOrServiceSubCategory(category, subCat));
            return user;
        }
    }

    @Override
    public ServiceCategoryBhatDto updateServiceCategory(ServiceCategoryBhat service) {
        String id = service.getId();
        String updatedBy = service.getUpdatedBy();
        String category = service.getServiceCategory();
        String subCategory = service.getServiceSubCategory();

        Optional<ServiceCategoryBhat> serviceCategory = serviceRepo.findById(id);
        if (serviceCategory.isPresent()) {
            serviceCategory.get().setUpdatedBy(updatedBy);
            serviceCategory.get().setServiceCategory(category);
            serviceCategory.get().setServiceSubCategory(subCategory);

            serviceRepo.save(serviceCategory.get());
            return getServiceById(id);
        }
        return null;
    }

    @Override
    public ServiceCategoryBhatDto getServiceById(String id) {
        if (serviceRepo.findById(id) != null) {
            Optional<ServiceCategoryBhat> serviceCat = serviceRepo.findById(id);
            ServiceCategoryBhat serviceCatData = serviceCat.get();
            ServiceCategoryBhatDto serviceDto = new ServiceCategoryBhatDto();

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
    public List<ServiceCategoryBhat> getServiceCategoryCivil(String category) {

        List<ServiceCategoryBhat> user = (serviceRepo.findByServiceCategoryOrderByCreateDateDesc(category));
        return user;

    }

    @Override
    public Page<ServiceCategoryBhat> getServiceCategoryNonCivil(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createDate").descending());
        return serviceRepo.findByServiceCategoryNot(category, pageable);
    }

    @Override
    public ServiceCategoryBhat deleteRecord(String id) {
        Optional<ServiceCategoryBhat> delete = Optional.empty();
        if (id != null) {
            delete = serviceRepo.findById(id);
        }

        if (delete.isPresent()) {
            serviceRepo.delete(delete.get());
            return delete.get();
        }
        return null;
    }

}
