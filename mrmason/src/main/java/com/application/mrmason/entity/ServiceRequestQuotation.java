package com.application.mrmason.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "header_quotation")
public class ServiceRequestQuotation {
	
	@Id
    @Column(name = "quotation_id", unique = true)
    private String quotationId;
	
	@Column(name = "request_id")
	private String requestId;

	@Column(name = "measurenames")
	private String measureNames;
	
	@Column(name = "value")
	private String value;

	@Column(name = "quoted_date")
	private Date quotedDate;
	
	@Column(name = "quotated_by")
	private String quotatedBy;

	@Enumerated(EnumType.STRING)
	@Column(name="status")
	private SPWAStatus status;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "updated_date")
	private Date updatedDate;
	
	@Column(name = "quotation_amount")
	private Integer quotationAmount;

}
