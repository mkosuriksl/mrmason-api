package com.application.mrmason.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.application.mrmason.dto.ServiceCategoryBhatDto;
import com.application.mrmason.entity.ServiceCategoryBhat;

public interface ServiceCategoryBhatService {
    ServiceCategoryBhatDto addServiceCategory(ServiceCategoryBhat service);

    List<ServiceCategoryBhat> getServiceCategory(String id, String category, String subCat);

    ServiceCategoryBhatDto updateServiceCategory(ServiceCategoryBhat service);

    ServiceCategoryBhatDto getServiceById(String id);

    List<ServiceCategoryBhat> getServiceCategoryCivil(String category);

    Page<ServiceCategoryBhat> getServiceCategoryNonCivil(String category, int page, int size);

    ServiceCategoryBhat deleteRecord(String id);
}
