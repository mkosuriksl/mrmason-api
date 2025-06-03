package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.PaymentSPTasksManagment;

import lombok.Data;

@Data
public class ResponseGetPaymentSPTasksDto {
	private String message;
	private boolean status;
	private List<PaymentSPTasksManagment> paymentSPTasksManagment;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;
	public ResponseGetPaymentSPTasksDto(String message, boolean status,
			List<PaymentSPTasksManagment> paymentSPTasksManagment, int currentPage, int pageSize, long totalElements,
			int totalPages) {
		super();
		this.message = message;
		this.status = status;
		this.paymentSPTasksManagment = paymentSPTasksManagment;
		this.currentPage = currentPage;
		this.pageSize = pageSize;
		this.totalElements = totalElements;
		this.totalPages = totalPages;
	}
	public ResponseGetPaymentSPTasksDto() {
		// TODO Auto-generated constructor stub
	}
}