package com.application.mrmason.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="admin_sp_qualification")
public class AdminSpQualification {
	
	@Id
	@Column(name="course_id")
	private String courseId;
	@Column(name="education_id")
	private String educationId;
	@Column(name="Name")
	private String name;
	@Column(name="branch_id")
	private String branchId;
	@Column(name="branch_name")
	private String branchName;
	@Column(name="updated_by")
	private String updatedBy;
	@UpdateTimestamp
	@Column(name="updated_date")
	private String updatedDate;
	
	
}
