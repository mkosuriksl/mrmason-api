package com.application.mrmason.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.ServiceCategoryMech;

@Repository
public interface ServiceCategoryMechRepo extends JpaRepository<ServiceCategoryMech, String>{
	List<ServiceCategoryMech>  findByIdOrderByCreateDateDesc(String id);
	List<ServiceCategoryMech>  findByServiceCategoryOrderByCreateDateDesc(String category);
	ServiceCategoryMech findByServiceCategoryAndServiceSubCategory(String serviceCategory, String serviceSubCategory);
}
