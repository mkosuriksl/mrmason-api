package com.application.mrmason.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;

import com.application.mrmason.entity.ServiceRequestCarpentaryQuotation;

public interface ServiceRequestCarpentaryQuotationRepository extends JpaRepository<ServiceRequestCarpentaryQuotation, String>{

	Collection<ServiceRequestCarpentaryQuotation> findByRequestId(String requestId);


}
