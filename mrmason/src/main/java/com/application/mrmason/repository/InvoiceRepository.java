package com.application.mrmason.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.application.mrmason.entity.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, String> {
}
