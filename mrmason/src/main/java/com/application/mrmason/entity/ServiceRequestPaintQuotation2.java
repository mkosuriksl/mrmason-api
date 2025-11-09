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
@Table(name = "workorder_details_table")
public class ServiceRequestPaintQuotation2 {
	
	@Id
	@Column(name = "admin_task_lineId")
	private String admintasklineId;
	
	@Column(name = "service_category")
	private String serviceCategory;

	@Column(name = "request_lineid_description")
	private String taskDescription;

	@Column(name = "quotation_id")
	private String quotationId;
	
	@Column(name = "task_id")
	private String taskId;

	@Column(name = "quoted_date")
	private Date quotedDate;
	
	@Column(name = "measurenames")
	private String measureNames;
	
	@Column(name = "value")
	private String value;

	@Enumerated(EnumType.STRING)
	@Column(name="status")
	private SPWAStatus status;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "updated_date")
	private Date updatedDate;

	@Column(name = "sp_id")
	private String spId;
	
    @Column(name = "workorder_id", unique = true)
    private String workOrderId;

}
