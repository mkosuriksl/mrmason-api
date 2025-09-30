package com.application.mrmason.dto;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.application.mrmason.entity.CustomerOrderHdrEntity;
import com.application.mrmason.enums.OrderStatus;


public interface CustomerOrderHdrRepo extends JpaRepository<CustomerOrderHdrEntity, String> {

	@Query("SELECT h FROM CustomerOrderHdrEntity h " +
		       "LEFT JOIN FETCH h.customerOrderDetailsEntities " +
		       "WHERE h.updatedBy = :customerId AND h.status = :status")
		Optional<CustomerOrderHdrEntity> findWithDetailsByUpdatedByAndStatus(
		        @Param("customerId") String customerId,
		        @Param("status") OrderStatus status);

}
