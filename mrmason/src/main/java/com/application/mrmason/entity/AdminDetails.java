package com.application.mrmason.entity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="mrmason_admin")
public class AdminDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private long id;
	@Column(name="admin_type")
	private String adminType;
	@Column(name="admin_name")
	private String adminName;
	@Column(name="mobile")
	private String mobile;
	@Column(name="email")
	private String email;
	@Column(name="password")
	private String password;
	@Builder.Default
	@Column(name="status")
	private String status="active";
	@Column(name="regdate")
	private String regDate;
	@Column(name="otp")
	private String otp;
	
	@PrePersist
	public void prePresist() {
		LocalDate now = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		this.regDate=now.format(formatter);
	}
}
