package com.application.mrmason.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;

import com.application.mrmason.entity.ServiceRequestPaintOnlyQuotation;

public interface ServiceRequestPaintOnlyQuotationRepository extends JpaRepository<ServiceRequestPaintOnlyQuotation, String>{

	Collection<ServiceRequestPaintOnlyQuotation> findByRequestId(String requestId);

}
