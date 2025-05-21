package com.application.mrmason.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;

import com.application.mrmason.entity.ServiceRequestPaintQuotation;

public interface ServiceRequestPaintQuotationRepository extends JpaRepository<ServiceRequestPaintQuotation, String>{

	Collection<ServiceRequestPaintQuotation> findByRequestId(String requestId);


}
