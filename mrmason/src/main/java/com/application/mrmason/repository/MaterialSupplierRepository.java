package com.application.mrmason.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.application.mrmason.entity.MaterialSupplier;
import com.application.mrmason.enums.Status;

import jakarta.transaction.Transactional;


public interface MaterialSupplierRepository extends JpaRepository<MaterialSupplier, String> {

	boolean existsBySupplierIdAndMaterialLineItem(String userId, String materialLineItem);
	
	 @Modifying
	    @Transactional
	    @Query("UPDATE MaterialSupplier d SET d.invoiceStatus = :invoiceStatus, d.invoiceNumber = :invoiceNumber WHERE d.quotationId = :quotationId")
	    int updateInvoiceStatusByQuotationId(@Param("quotationId") String quotationId,
	                                         @Param("invoiceStatus") Status invoiceStatus,
	                                         @Param("invoiceNumber") String invoiceNumber);


}
