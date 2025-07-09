package com.application.mrmason.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.ServiceCategory;
@Repository
public interface ServiceCategoryRepo extends JpaRepository<ServiceCategory,String>{
	List<ServiceCategory>  findByIdOrderByCreateDateDesc(String id);
	List<ServiceCategory> findByServiceCategoryOrServiceSubCategory(String serviceCategory, String serviceSubCategory);
	ServiceCategory findByServiceCategoryAndServiceSubCategory(String serviceCategory, String serviceSubCategory);
	
//	List<ServiceCategory>  findByServiceCategoryNotOrderByCreateDateDesc(String category);
	Page<ServiceCategory> findByServiceCategoryNot(String category, Pageable pageable);
	List<ServiceCategory> findByServiceCategoryOrderByCreateDateDesc(String category);
	
	Optional<ServiceCategory> findById(String id);
    Optional<ServiceCategory> findByServiceSubCategory(String serviceSubCategory);
	
}