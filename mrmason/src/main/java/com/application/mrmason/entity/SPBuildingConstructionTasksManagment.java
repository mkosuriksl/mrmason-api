package com.application.mrmason.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
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
@Table(name = "sp_building_construction_tasks_managemnt")
public class SPBuildingConstructionTasksManagment {

	@Id
	@Column(name = "userId_taskId")
	private String userIdServiceCategoryTaskId;

	@Column(name = "service_category")
	private String serviceCategory;

	@Column(name = "task_name")
	private String taskName;

	@Column(name = "task_id")
	private String taskId;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "updated_date")
	private Date updatedDate;
	
	@Column(name = "user_id")
	private String userId;
	
	@Column(name = "measure_name")
	private String measureName;
	
	@Column(name = "value")
	private String value;
	
	@PrePersist
	private void prePersist() {

	}

}
