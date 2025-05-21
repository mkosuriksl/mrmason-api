package com.application.mrmason.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;

import com.application.mrmason.entity.ServiceRequestPlumbingQuotation;

public interface ServiceRequestPlumbingQuotationRepository extends JpaRepository<ServiceRequestPlumbingQuotation, String>{

	Collection<ServiceRequestPlumbingQuotation> findByRequestId(String requestId);

}
