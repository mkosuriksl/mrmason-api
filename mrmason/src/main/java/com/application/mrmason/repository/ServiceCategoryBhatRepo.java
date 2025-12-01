package com.application.mrmason.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.ServiceCategoryBhat;

@Repository
public interface ServiceCategoryBhatRepo extends JpaRepository<ServiceCategoryBhat, String> {
    ServiceCategoryBhat findByServiceCategoryAndServiceSubCategory(String serviceCategory, String serviceSubCategory);

    List<ServiceCategoryBhat> findByIdOrderByCreateDateDesc(String id);

    List<ServiceCategoryBhat> findByServiceCategoryOrServiceSubCategory(String category, String subCat);

    List<ServiceCategoryBhat> findByServiceCategoryOrderByCreateDateDesc(String category);

    Page<ServiceCategoryBhat> findByServiceCategoryNot(String category, Pageable pageable);
}
