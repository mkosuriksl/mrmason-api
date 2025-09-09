package com.application.mrmason.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "invoice")
public class Invoice {

    @Id
    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name = "cmatmaterial_requestid")
    private String cmatRequestId;

    @Column(name = "quoted_amount")
    private BigDecimal quotedAmount;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "invoice_date")
    private LocalDate invoiceDate;
}
