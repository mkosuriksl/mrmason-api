package com.application.mrmason.entity;

import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "quotation")
public class QuotationEntity {

	@Id
	@Column(name = "req_id", unique = true)
	private String reqId;
	@Column(name = "customer_id")
	private String customerId;
	@Column(name = "unit")
	private String unit;
	@Column(name = "quoted_amount")
	private Integer quotedAmount;
	@Column(name = "sp_id")
	private String servicePersonId;
	@Column(name = "updated_by")
	private String updatedBy;
	@Column(name = "updated_date")
	private Date updatedDate;
	@Column(name = "status")
	private String status;

}
