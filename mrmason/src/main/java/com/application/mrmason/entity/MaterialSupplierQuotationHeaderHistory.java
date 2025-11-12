package com.application.mrmason.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.application.mrmason.enums.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "material_supplier_quotation_header_updated_by_customer")
public class MaterialSupplierQuotationHeaderHistory {

	@Id
	@Column(name = "cmatmaterial_requestid")
	private String cmatRequestId;; 

	@Column(name = "qutotation_id")
	private String quotationId; 


	@Column(name = "quoted_amount")
	private BigDecimal quotedAmount;
	
	@Column(name = "supplier_id")
	private String supplierId; 

	@Column(name = "quoted_date")
	private LocalDate quotedDate; 

	@Column(name = "updated_date")
	private LocalDate updatedDate; 
	
//	@Column(name = "invoice_number")
//	private String invoiceNumber;
	
//	@Column(name = "invoice_status")
//	private Status invoiceStatus;
	
	@Column(name = "quotation_status")
	private Status quotationStatus;
	
//	@Column(name = "invoice_date")
//	private LocalDate invoiceDate;
	
	@Column(name = "updated_by")
	private String updatedBy;
	
    @Column(name = "user_type")
    private String userType; 
	
}
