package com.application.mrmason.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.ServiceCategory;
@Repository
public interface ServiceCategoryRepo extends JpaRepository<ServiceCategory,String>{
	List<ServiceCategory>  findByIdOrderByCreateDateDesc(String id);
	List<ServiceCategory>  findByServiceCategoryOrderByCreateDateDesc(String category);
	ServiceCategory findByServiceCategoryAndServiceSubCategory(String serviceCategory, String serviceSubCategory);
	
	
}