package com.application.mrmason.entity;

import java.util.Date;

import com.application.mrmason.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer_order_method_details")
public class CustomerOrderDetailsEntity {
	@Id
	@Column(name = "Orderline_Id")
	private String orderlineId;
	
	@Column(name = "brand")
	private String brand;
	
	@Column(name = "skuId_userId")
	private String skuIdUserId;

	@Column(name = "mrp")
	private Double  mrp;
	
	@Column(name = "discount")
	private Integer  discount;

	@Column(name = "gst")
	private Integer gst;

	@Column(name = "total")
	private Double total;


	@Column(name = "orderQty")
	private Integer orderQty;

//	@Column(name = "prescriptionRequired")
//	private String prescriptionRequired;

	@Column(name = "updated_date")
	private Date updatedDate;

	@Column(name = "updatedBy")
	private String updatedBy;

	@ManyToOne
	@JsonBackReference
	@JoinColumn(name = "orderId", referencedColumnName = "orderId")
	private CustomerOrderHdrEntity customerOrderOrderHdrEntity;

	@Column(name = "msuserid")
	private String msUserId;
	
    @Column(name = "Status")
    private OrderStatus status;
	
}
