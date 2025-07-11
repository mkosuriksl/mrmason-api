package com.application.mrmason.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.application.mrmason.entity.AdminSpQualification;

public interface AdminSpQualificationService {

	public AdminSpQualification addQualification(AdminSpQualification qualification) ;
	public AdminSpQualification update(AdminSpQualification update);
//	public List<AdminSpQualification> getQualification(String courseId, String educationId, String name,
//			String branchId, String branchName);
	public Page<AdminSpQualification> getQualification(String courseId, String educationId, String name,
			String branchId, String branchName,Pageable pageable);
	public List<AdminSpQualification> getAllQualifications();
}
