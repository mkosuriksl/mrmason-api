package com.application.mrmason.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.application.mrmason.entity.MaterialRequirementByRequest;

public interface MaterialRequirementByRequestRepository extends JpaRepository<MaterialRequirementByRequest, String> {
	int countByReqId(String reqId);
}
