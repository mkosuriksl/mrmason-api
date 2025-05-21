package com.application.mrmason.entity;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
	@Column(name="rec_id",nullable = false)
	private String recId;
	
	@Column(name="workerId_workOrdId_line",nullable = false)
	private String workerIdWorkOrdIdLine;
	
	@Column(name="work_ord_id")
	private String workOrdId;
	@Column(name="worker_id")
	private String workerId;
	@Column(name="date_of_work")
	private String dateOfWork;
	@Column(name="end_date_of_work")
	private String endDateOfWork;
	@Column(name="updated_by")
	private String updatedBy;
	@Column(name="updated_date")
	private Date updatedDate;
	@Column(name="amount")
	private Integer amount;
	@Column(name="payment_status")
	private String paymentStatus;
	@Column(name="payment_method")
	private String 	paymentMethod;
	@Column(name="currency")
	private String currency;
	@Column(name="location")
	private String location;
	@Column(name="available")
	private String available;
	@Enumerated(EnumType.STRING)
	@Column(name="status")
	private SPWAStatus status;
	@Column(name="spid")
	private String spId;
//	@PrePersist
//	private void prePersist() {
//		if (this.workerIdWorkOrdIdLine == null) {
//	        this.workerIdWorkOrdIdLine = this.workerId + "_" + this.workOrdId + "_0001";
//	    }
//		LocalDateTime now = LocalDateTime.now();
//		String year = String.valueOf(now.getYear());
//		String month = String.format("%02d", now.getMonthValue());
//		String day = String.format("%02d", now.getDayOfMonth());
//		String hour = String.format("%02d", now.getHour());
//		String minute = String.format("%02d", now.getMinute());
//		String second = String.format("%02d", now.getSecond());
//		String millis = String.format("%03d", now.getNano() / 1000000).substring(0, 2);
//		this.recId = "SPWA" + "_"+ year + month + day + hour + minute + second + millis;
//	}
	@PrePersist
	private void prePersist() {
	    if (this.workerIdWorkOrdIdLine == null) {
	        this.workerIdWorkOrdIdLine = this.workerId + "_" + this.workOrdId + "_0001";
	    }
	    this.recId = "SPWA_" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);
	}

	
}
