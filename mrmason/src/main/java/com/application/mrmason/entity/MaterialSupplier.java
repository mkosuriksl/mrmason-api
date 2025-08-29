package com.application.mrmason.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.application.mrmason.enums.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity

@Table(name = "material_supplier")
public class MaterialSupplier {

	@Id
	@Column(name = "material_line_item")
	private String materialLineItem; 

	@Column(name = "qutotation_id")
	private String quotationId; 

	@Column(name = "customer_order")
	private String customerOrder; 

	@Column(name = "mrp")
	private BigDecimal mrp;

	@Column(name = "discount")
	private BigDecimal discount; 

	@Column(name = "quoted_amount")
	private BigDecimal quotedAmount;
	@Column(name = "supplier_id")
	private String supplierId; 

	@Column(name = "quoted_date")
	private LocalDate quotedDate; 

	@Column(name = "updated_date")
	private LocalDate updatedDate; 

	@Column(name = "status")
	private Status status;
	
	@Column(name = "gst")
	private double gst;
	
	@PrePersist
    public void prePersist() {
        if (this.quotationId == null) {
            // Generate random 6-digit number
            int randomSixDigits = (int)(Math.random() * 900000) + 100000;
            this.quotationId = supplierId + "_" + randomSixDigits;
        }
        if (this.updatedDate == null) {
            this.updatedDate = LocalDate.now();
        }
    }
}
