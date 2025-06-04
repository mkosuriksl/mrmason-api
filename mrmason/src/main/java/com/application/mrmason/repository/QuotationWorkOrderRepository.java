package com.application.mrmason.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.QuotationWorkOrder;

@Repository
public interface QuotationWorkOrderRepository extends JpaRepository<QuotationWorkOrder, String> {
    Optional<QuotationWorkOrder> findByQuotationWorkOrder(String quotationWorkOrder);
}
