package com.application.mrmason.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
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
@Table(name = "c_registration")
public class CustomerRegistration {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	@Column(name = "userid")
	private String userid;
	@Column(name = "useremail")
	private String userEmail;
	@Column(name = "usermobile")
	private String userMobile;
	@Column(name = "userpassword")
	@Transient
	private String userPassword;
	@Column(name = "usertype")
	private String usertype;
	@Column(name = "username")
	private String userName;
	@Column(name = "usertown")
	private String userTown;
	@Column(name = "userdistrict")
	private String userDistrict;
	@Column(name = "userstate")
	private String userState;
	@Column(name = "userpincode")
	private String userPincode;
	@CreationTimestamp
	@Column(name = "regdate")
	private String regDate;


	@PrePersist
	private void prePersist() {
		LocalDateTime now = LocalDateTime.now();
		String year = String.valueOf(now.getYear());
		String month = String.format("%02d", now.getMonthValue());
		String day = String.format("%02d", now.getDayOfMonth());
		String hour = String.format("%02d", now.getHour());
		String minute = String.format("%02d", now.getMinute());
		String second = String.format("%02d", now.getSecond());
		String millis = String.format("%03d", now.getNano() / 1000000).substring(0, 2); 
		this.userid ="CU"+ year + month + day + hour + minute + second+millis;
	}

}
