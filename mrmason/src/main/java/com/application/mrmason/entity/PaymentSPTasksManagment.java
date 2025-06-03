package com.application.mrmason.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "payment_sp_task")
public class PaymentSPTasksManagment {

	@Id
	@Column(name = "request_lineid")
	private String requestLineId;

	@Column(name = "task_name")
	private String taskName;

	@Column(name = "amount")
	private Integer amount;

	@Column(name = "workPersentage")
	private Integer workPersentage ;

	@Column(name = "amountPersentage")
	private Integer amountPersentage;

	@Column(name = "daily_labor_pay")
	private String dailylaborPay;
	
	@Column(name = "advanced_payment")
	private String advancedPayment;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "updated_date")
	private Date updatedDate;

	@Column(name = "sp_id")
	private String spId;

}
