package com.application.mrmason.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.application.mrmason.entity.ServiceRequestPaintQuotation;

public interface ServiceRequestPaintQuotationRepository extends JpaRepository<ServiceRequestPaintQuotation, String>{

	Integer findMaxCounterByTaskIdAndRequestId(String taskId, String requestId);

	Collection<ServiceRequestPaintQuotation> findByTaskId(String taskId);


}
