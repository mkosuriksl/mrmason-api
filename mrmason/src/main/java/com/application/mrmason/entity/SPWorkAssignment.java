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
@Table(name="sp_work_assignment")
public class SPWorkAssignment {

	@Id
	@Column(name="rec_id")
	private String recId;
	@Column(name="work_ord_id")
	private String servicePersonId;
	@Column(name="worker_id")
	private String workerId;
	@Column(name="date_of_work")
	private String dateOfWork;
	@Column(name="updated_by")
	private String updatedBy;
	@Column(name="updated_date")
	private Date updatedDate;
	@Column(name="amount")
	private Integer amount;
	
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
		this.recId ="OR"+ year + month + day + hour + minute + second+millis;
	}
	
}
