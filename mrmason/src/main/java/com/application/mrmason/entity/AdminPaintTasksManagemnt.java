package com.application.mrmason.entity;

import java.time.LocalDateTime;
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
@Table(name = "admin_paint_tasks_managemnt")
public class AdminPaintTasksManagemnt {

	@Id
	@Column(name = "request_lineid")
	private String adminTaskId;

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

	@PrePersist
	private void prePersist() {
		LocalDateTime now = LocalDateTime.now();
		String year = String.valueOf(now.getYear());
		String month = String.format("%02d", now.getMonthValue());
		String day = String.format("%02d", now.getDayOfMonth());
		String hour = String.format("%02d", now.getHour());
		String minute = String.format("%02d", now.getMinute());
		String second = String.format("%02d", now.getSecond());
		String millis = String.format("%03d", now.getNano() / 1000000).substring(0, 2); 
		this.adminTaskId ="ADM"+ year + month + day + hour + minute + second+millis;

	}

}
