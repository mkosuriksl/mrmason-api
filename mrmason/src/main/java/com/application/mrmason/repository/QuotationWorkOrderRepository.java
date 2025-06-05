package com.application.mrmason.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.QuotationWorkOrder;

@Repository
public interface QuotationWorkOrderRepository
extends JpaRepository<QuotationWorkOrder, String>, JpaSpecificationExecutor<QuotationWorkOrder> {
    Optional<QuotationWorkOrder> findByQuotationWorkOrder(String quotationWorkOrder);
}
