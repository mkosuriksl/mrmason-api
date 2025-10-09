package com.application.mrmason.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "customer_to_retailer_order_detail")
public class CustomerRetailerOrderDetailsEntity {
    @Id
    private String lineItemId;
    
    private String customerCartOrderLineId;
	private String brand;
    private int orderQty;
    private Double mrp;
    private float discount;
    private float gst;
    private Double totalAmount;
    private String manufactureName;
	private String skuIdUserId;
	private String prescriptionRequired;
	private Date updatedDate;
	private String updatedBy;
	private String userId;
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name="orderId",referencedColumnName = "orderId")
    private CustomerRetailerOrderHdrEntity customerRetailerOrderHdr;

}
