package com.application.mrmason.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;

import com.application.mrmason.entity.ServiceRequestElectricalQuotation;

public interface ServiceRequestElectricalQuotationRepository
		extends JpaRepository<ServiceRequestElectricalQuotation, String> {

	Collection<ServiceRequestElectricalQuotation> findByRequestId(String requestId);


}
