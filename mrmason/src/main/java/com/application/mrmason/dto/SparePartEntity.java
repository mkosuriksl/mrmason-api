package com.application.mrmason.dto;

import java.util.Date;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "admin_service")
@Getter
@Setter
public class SparePartEntity {

	@Id
	@Column(name = "request_id_sku_id")
	private String requestIdSkuId;

	private String requestId;
	private String sparePart;
	private String skuId;
	private String brand;
	private String model;
	private Double amount;
	private Double discount;
	private Double gst;
	private Double totalAmount;
	private String warranty;
	private String userId;

	private String updatedBy;

	@UpdateTimestamp
	private Date updatedDate;

	@PrePersist
	public void autoGenerateCompositeKey() {

		if (requestId != null && skuId != null) {
			this.requestIdSkuId = requestId + "_" + skuId;
		}
	}
}
