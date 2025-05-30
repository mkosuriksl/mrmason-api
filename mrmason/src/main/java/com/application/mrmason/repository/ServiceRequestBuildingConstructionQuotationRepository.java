package com.application.mrmason.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.application.mrmason.entity.ServiceRequestBuildingConstructionQuotation;
import com.application.mrmason.entity.ServiceRequestPaintQuotation;
import com.application.mrmason.entity.ServiceRequestQuotation;

public interface ServiceRequestBuildingConstructionQuotationRepository extends JpaRepository<ServiceRequestBuildingConstructionQuotation, String>{

	
	Collection<ServiceRequestBuildingConstructionQuotation> findByRequestId(String requestId);


}
