package com.application.mrmason.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.application.mrmason.entity.ServiceRequestQuotation;

public interface ServiceRequestQuotationRepository extends JpaRepository<ServiceRequestQuotation,String>{

	List<ServiceRequestQuotation> findByRequestId(String requestId);
	@Query("SELECT q FROM ServiceRequestQuotation q WHERE q.requestId = :requestId")
	List<ServiceRequestQuotation> findByRequestIds(@Param("requestId") String requestId);

}
