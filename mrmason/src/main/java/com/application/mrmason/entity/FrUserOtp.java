package com.application.mrmason.entity;

import java.time.LocalDateTime;

import com.application.mrmason.enums.RegSource;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "fr_user_otp")
public class FrUserOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long otpSeqNo;

    private String frUserid;
    private String frEmailOtp;
    private String frMobileOtp;

    @Column(nullable = false, unique = true)
    private String frEmail;
    @Column(nullable = true, unique = true)
    private String frMobile;
    @Enumerated(EnumType.STRING)
    private RegSource regSource;

	@Column(name = "USER_TYPE")
	@Enumerated(EnumType.STRING)
	private UserType userType;
    private LocalDateTime updatedDate;
    private String updatedBy;
    
    @Column(name = "email_verified", length = 5)
    private String emailVerified; // "yes" / "no"

    @Column(name = "mobile_verified", length = 5)
    private String mobileVerified;
}

