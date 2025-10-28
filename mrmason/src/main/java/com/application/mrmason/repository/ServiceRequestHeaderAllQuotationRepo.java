package com.application.mrmason.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.ServiceRequestHeaderAllQuotation;

@Repository
public interface ServiceRequestHeaderAllQuotationRepo extends JpaRepository<ServiceRequestHeaderAllQuotation, String> {

	List<ServiceRequestHeaderAllQuotation> findByRequestId(String requestId);
	@Query("SELECT q FROM ServiceRequestHeaderAllQuotation q WHERE q.requestId = :requestId")
	List<ServiceRequestHeaderAllQuotation> findByRequestIds(@Param("requestId") String requestId);
}
