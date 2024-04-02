package com.applicaion.mrmason.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name ="users")
@Builder
public class User {

	@Id
	@Column(name = "BOD_SEQ_NO")
	public String bodSeqNo;
	@Column(name = "NAME")
	public String name;
	@Column(name = "BUSINESS_NAME")
	public String businessName;
	@Column(name = "MOBILE_NO")
	public String mobile;
	@Column(name = "EMAIL_ID")
	public String email;
	@Column(name = "PASSWORD")
	public String password;
	@Column(name = "ADDRESS")
	public String address;
	@Column(name = "CITY")
	public String city;
	@Column(name = "DISTRICT")
	public String district;
	@Column(name = "STATE")
	public String state;
	@Column(name = "PINCODE_NO")
	public String pincodeNo;


	@Transient
	public LocalDateTime update;
	
	@Column(name = "UPDATE_DATETIME")
	public String updatedDate;



	@CreationTimestamp
	@Column(name = "REGISTRATION_DATETIME")
	public Date registeredDate;

	@Column(name = "VERIFIED")
	@Builder.Default
	public String verified ="no";
	@Column(name = "SERVICE_CATEGORY")
	public String serviceCategory;
	@Column(name = "USER_TYPE")
	@Builder.Default
	private String userType ="Developer";
	@Column(name = "STATUS")
	@Builder.Default
	private String status = "active";

	@PrePersist
	private void prePersist() {
		LocalDateTime now = LocalDateTime.now();
		String year = String.valueOf(now.getYear());
		String month = String.format("%02d", now.getMonthValue()); 
		String day = String.format("%02d", now.getDayOfMonth()); 
		String hour = String.format("%02d", now.getHour()); 
		String minute = String.format("%02d", now.getMinute()); 
		String second = String.format("%02d", now.getSecond()); 
		this.bodSeqNo = "SP" + year + month + day + hour + minute + second;
		
		
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");
		this.update = now;
		this.updatedDate= now.format(formatter);

	}



	
}
