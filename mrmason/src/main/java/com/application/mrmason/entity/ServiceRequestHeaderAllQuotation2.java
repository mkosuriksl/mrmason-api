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
@Table(name = "workorder_header")
public class ServiceRequestHeaderAllQuotation2 {
	
	@Id
    @Column(name = "workorder_id", unique = true)
    private String workOrderId;
	
	@Column(name = "quotation_id")
	private String quotationId;

	@Column(name = "quoted_date")
	private Date quotedDate;
	
	@Enumerated(EnumType.STRING)
	@Column(name="status")
	private SPWAStatus status;
	
	@Column(name = "sp_id")
	private String spId;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "updated_date")
	private Date updatedDate;

}
