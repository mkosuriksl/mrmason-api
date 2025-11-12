package com.application.mrmason.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.MaterialSupplierQuotationHeaderHistory;

@Repository
public interface MaterialSupplierQuotationHeaderHistoryRepo extends JpaRepository<MaterialSupplierQuotationHeaderHistory, String>{

}
