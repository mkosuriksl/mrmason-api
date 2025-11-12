package com.application.mrmason.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.application.mrmason.entity.MaterialSupplierQuotationHeader;
import com.application.mrmason.enums.Status;

import jakarta.transaction.Transactional;


public interface MaterialSupplierQuotationHeaderRepository extends JpaRepository<MaterialSupplierQuotationHeader, String> {

	 @Modifying
	    @Transactional
	    @Query("UPDATE MaterialSupplierQuotationHeader h SET h.invoiceStatus = :invoiceStatus, h.invoiceNumber = :invoiceNumber WHERE h.quotationId = :quotationId")
	    int updateInvoiceStatusByQuotationId(@Param("quotationId") String quotationId,
	                                         @Param("invoiceStatus") Status invoiceStatus,
	                                         @Param("invoiceNumber") String invoiceNumber);

	MaterialSupplierQuotationHeader findByCmatRequestId(String cmatRequestId);

}
