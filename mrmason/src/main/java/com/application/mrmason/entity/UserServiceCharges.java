package com.application.mrmason.entity;

import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="user_service_charges")

public class UserServiceCharges {

		@Id
		@Column(name="sp_service_charge_key")
		private String serviceChargeKey;
		@Column(name="service_id")
		private String serviceId;
		@Column(name="service_charge")
		private int serviceCharge;
		@Column(name="location")
		private String location;
		@UpdateTimestamp
		@Column(name="updated_date")
		private String updatedDate;
		@Column(name="updated_by")
		private String updatedBy;
		@Column(name="brand")
		private String brand;
		@Column(name="model")
		private String model;
		@Column(name="subcategory")
		private String subcategory;
		
		
		@Transient
		private String bodSeqNo;
		


	}


