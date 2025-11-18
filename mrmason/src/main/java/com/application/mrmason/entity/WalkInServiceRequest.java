package com.application.mrmason.entity;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Table(name = "walk_in_service_request")
public class WalkInServiceRequest {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "req_seq_id")
	private long reqSeqId;

	@Column(name = "service_sub_category")
	private String serviceSubCategory;

	@Column(name="service_name")
	private String serviceName;
	
	@Column(name = "request_id")
	private String requestId;

	@CreationTimestamp
	@Column(name = "service_request_date")
	private String serviceRequestDate;

	@Column(name = "requested_by")
	private String requestedBy;

	@Column(name = "req_pincode")
	private String location;

	@Column(name = "description") // Corrected the column name
	private String description;
	
	@Builder.Default
	@Column(name = "status")
	private String status = "NEW";

	@Column(name = "service_date")
	private String serviceDateDb;

	@Column(name = "assetid")
	private String assetId;
	
	@Column(name = "booked_by")
	private String bookedBy;
	
	@Column(name = "requested_mode")
	private String requestedMode="WALK-IN";

	@PrePersist
	private void prePersist() {
		LocalDateTime now = LocalDateTime.now();
		String year = String.valueOf(now.getYear());
		String month = String.format("%02d", now.getMonthValue());
		String day = String.format("%02d", now.getDayOfMonth());
		String hour = String.format("%02d", now.getHour());
		String minute = String.format("%02d", now.getMinute());
		String second = String.format("%02d", now.getSecond());
		this.requestId = "RE"+year + month + day + hour + minute + second;

		DateTimeFormatter formatterExp = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		this.serviceDateDb = now.format(formatterExp);

	}
}
