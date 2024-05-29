package com.application.mrmason.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.application.mrmason.entity.ServiceRequest;

@Repository
public interface ServiceRequestRepo extends JpaRepository<ServiceRequest, Long>{
	List<ServiceRequest> findByRequestedByOrderByReqSeqIdDesc(String userId);
	List<ServiceRequest> findByServiceNameOrderByReqSeqIdDesc(String serviceName);
	List<ServiceRequest> findByAssetIdOrderByReqSeqIdDesc(String assetId);
	List<ServiceRequest> findByLocationOrderByReqSeqIdDesc(String location);
	List<ServiceRequest> findByStatusOrderByReqSeqIdDesc(String status);
	ServiceRequest findByRequestId(String requestId);
	@Query("SELECT cr FROM ServiceRequest cr WHERE cr.serviceRequestDate BETWEEN :startDate AND :endDate")
	List<ServiceRequest> findByServiceRequestDateBetween(String startDate, String endDate);
	
}
