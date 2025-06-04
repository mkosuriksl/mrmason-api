package com.application.mrmason.entity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "quotation_work_order")
public class QuotationWorkOrder {

	@Id
	@Column(name = "quotation_work_order")
	private String quotationWorkOrder;

	@Column(name = "quotation_id", unique = true)
	private String quotationId;

	@Column(name = "wo_generate_date")
	private Date woGenerateDate;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "updated_date")
	private Date updatedDate;

	@Column(name = "expected_start_date")
	private Date expectedStartDate;
	
	@Column(name = "expected_end_date")
	private Date expectedEndDate;

	@Column(name = "sp_id")
	private String spId;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private SPWAStatus status;
	
	@PrePersist
	public void generateQuotationWorkOrderId() {
		if (this.quotationWorkOrder == null || this.quotationWorkOrder.isEmpty()) {
			String timestamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
			int randomDigits = new Random().nextInt(9000) + 1000; // ensures 4-digit random number
			this.quotationWorkOrder = "WO" + timestamp + randomDigits;
		}
	}
}
