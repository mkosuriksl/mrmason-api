package com.application.mrmason.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.application.mrmason.entity.ServiceRequestPaintQuotation2;

public interface ServiceRequestPaintQuotationRepository2 extends JpaRepository<ServiceRequestPaintQuotation2, String>{

	List<ServiceRequestPaintQuotation2> findByWorkOrderId(String workOrder);


}
