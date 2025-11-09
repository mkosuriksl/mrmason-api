package com.application.mrmason.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.ServiceRequestHeaderAllQuotation2;

@Repository
public interface ServiceRequestHeaderAllQuotationRepo2 extends JpaRepository<ServiceRequestHeaderAllQuotation2, String> {

	List<ServiceRequestHeaderAllQuotation2> findByQuotationId(String quotationId);
	@Query("SELECT q FROM ServiceRequestHeaderAllQuotation2 q WHERE q.workOrderId = :workOrderId")
	List<ServiceRequestHeaderAllQuotation2> findByWorkOrderIds(@Param("workOrderId") String workOrderId);
	Optional<ServiceRequestHeaderAllQuotation2> findByWorkOrderId(String workOrderId);
}
