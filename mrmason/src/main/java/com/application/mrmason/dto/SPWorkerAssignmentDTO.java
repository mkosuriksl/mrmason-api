package com.application.mrmason.dto;

import com.application.mrmason.entity.SPWAStatus;
import com.application.mrmason.entity.SPWorkAssignment;

import lombok.Data;

@Data
public class SPWorkerAssignmentDTO {
	private String recId;
	private String workerIdWorkOrdIdLine;
	private String workOrdId;
	private String workerId;
	private String dateOfWork;
	private String endDateOfWork;
	private String updatedBy;
	private String updatedDate;
	private int amount;
	private String paymentStatus;
	private String paymentMethod;
	private String currency;
	private String location;
	private String available;
	private SPWAStatus status;
	private String spId;
	private String workerName;
	private String workPhoneNum;

	public SPWorkerAssignmentDTO(SPWorkAssignment assignment, String workerName, String workPhoneNum) {
		this.recId = assignment.getRecId();
		this.workerIdWorkOrdIdLine = assignment.getWorkerIdWorkOrdIdLine();
		this.workOrdId = assignment.getWorkOrdId();
		this.workerId = assignment.getWorkerId();
		this.dateOfWork = assignment.getDateOfWork().toString();
		this.endDateOfWork = assignment.getEndDateOfWork().toString();
		this.updatedBy = assignment.getUpdatedBy();
		this.updatedDate = assignment.getUpdatedDate().toString();
		this.amount = assignment.getAmount();
		this.paymentStatus = assignment.getPaymentStatus();
		this.paymentMethod = assignment.getPaymentMethod();
		this.currency = assignment.getCurrency();
		this.location = assignment.getLocation();
		this.available = assignment.getAvailable();
		this.status = assignment.getStatus();
		this.spId = assignment.getSpId();
		this.workerName = workerName;
		this.workPhoneNum = workPhoneNum;
	}
}
