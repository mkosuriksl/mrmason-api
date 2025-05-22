package com.application.mrmason.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;

import com.application.mrmason.entity.ServiceRequestBuildingConstructionQuotation;

public interface ServiceRequestBuildingConstructionQuotationRepository extends JpaRepository<ServiceRequestBuildingConstructionQuotation, String>{

	Collection<ServiceRequestBuildingConstructionQuotation> findByRequestId(String requestId);


}
