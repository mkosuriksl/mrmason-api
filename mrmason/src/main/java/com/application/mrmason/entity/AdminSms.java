package com.application.mrmason.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(name = "admin_sms")
public class AdminSms {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String serviceProvideName;

	private String apiKey;

	private String sender;

	private String url;
	
	private String templateId;
	
	private String smsText;
	
	private String regSource;
	
	private boolean active;

	private String updatedBy;
	
	private LocalDateTime createdDate;

	private LocalDateTime updatedDate;
	

}
