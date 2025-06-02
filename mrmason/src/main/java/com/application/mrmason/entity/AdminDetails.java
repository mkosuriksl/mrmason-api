package com.application.mrmason.entity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "mrmason_admin")
public class AdminDetails implements UserDetails {

	private static final long serialVersionUID = 5342329L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	@Column(name = "admin_type")
	@Enumerated(EnumType.STRING)
	private UserType userType;
	@Column(name = "admin_name")
	private String adminName;
	@Column(name = "mobile")
	private String mobile;
	@Column(name = "email")
	private String email;
	@Column(name = "password")
	private String password;
	@Builder.Default
	@Column(name = "status")
	private String status = "active";
	@Column(name = "regdate")
	private String regDate;
	@Column(name = "otp")
	private String otp;
	@Column(name = "adminId", nullable = false, unique = true)
	private String adminId;
	
	@PrePersist
	public void prePresist() {
		LocalDate now = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		this.regDate = now.format(formatter);
		
		if (this.adminId == null || this.adminId.isEmpty()) {
			int randomNum = (int)(Math.random() * 900000) + 100000; // generates a number between 100000 and 999999
			this.adminId = "Adm" + randomNum;
		}
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + getUserType().name()));

	}

	@Override
	public String getUsername() {
		return this.email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
