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
@Table(name = "work_breakdown_elements")
public class WorkBreakdownElements {

	@Id
	@Column(name = "sub_task_id")
	private String subTaskId;
	
	@Column(name = "wo_order_no")
	private String woOrderNo;

	@Column(name = "task_id")
	private String taskId;

	@Column(name = "actual_start_date")
	private Date actualStartDate;

	@Column(name = "actual_end_date")
	private Date actualEndDate;

	@Column(name = "tentative_startdate")
	private Date tentativeStartdate;

	@Column(name = "tentaive_enddate")
	private Date tentaiveEnddate;
	
	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "updated_date")
	private Date updatedDate;
	
	@Enumerated(EnumType.STRING)
	@Column(name="status")
	private SPWAStatus status;

}
