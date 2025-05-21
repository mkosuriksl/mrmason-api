package com.application.mrmason.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.application.mrmason.entity.ServiceRequestElectricalQuotation;

public interface ServiceRequestElectricalQuotationRepository
		extends JpaRepository<ServiceRequestElectricalQuotation, Long> {

}
