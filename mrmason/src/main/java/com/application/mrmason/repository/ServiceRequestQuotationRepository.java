package com.application.mrmason.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.ServiceRequestQuotation;

@Repository
public interface ServiceRequestQuotationRepository extends JpaRepository<ServiceRequestQuotation,String>{

	List<ServiceRequestQuotation> findByRequestId(String requestId);
	@Query("SELECT q FROM ServiceRequestQuotation q WHERE q.requestId = :requestId")
	List<ServiceRequestQuotation> findByRequestIds(@Param("requestId") String requestId);
	
	@Query("SELECT q FROM ServiceRequestQuotation q WHERE q.quotationId = :quotationId")
	List<ServiceRequestQuotation> findByQuotationIds(@Param("quotationId") String quotationId);

}
