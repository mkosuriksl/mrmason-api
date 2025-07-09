package com.application.mrmason.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.AdminServiceName;

@Repository
public interface AdminServiceNameRepo extends JpaRepository<AdminServiceName, String>{
	 Page<AdminServiceName> findByServiceIdOrServiceNameOrServiceSubCategoryOrderByAddedDateDesc(String serviceId, String serviceName, String serviceSubCat,Pageable pageable);
	 AdminServiceName findByServiceId(String serviceId);
	List<AdminServiceName> findByServiceIdIn(List<String> serviceIds);
	
	
}


