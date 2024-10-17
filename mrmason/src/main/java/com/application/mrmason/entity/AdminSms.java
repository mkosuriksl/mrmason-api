package com.application.mrmason.entity;

import java.time.LocalDateTime;

import com.application.mrmason.enums.RegSource;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
	
	@Enumerated(EnumType.STRING)
	private RegSource regSource;
	
	private boolean active;

	private String updatedBy;
	
	private LocalDateTime createdDate;

	private LocalDateTime updatedDate;
	

}
