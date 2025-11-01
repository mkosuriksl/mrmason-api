package com.application.mrmason.repository;

import com.application.mrmason.entity.FrUserOtp;
import com.application.mrmason.enums.RegSource;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FrUserOtpRepository extends JpaRepository<FrUserOtp, Long> {
    Optional<FrUserOtp> findByFrUserid(String userId);

	Optional<FrUserOtp> findByFrEmailAndRegSource(String emailOrMobile, RegSource regSource);

	Optional<FrUserOtp> findByFrMobileAndRegSource(String emailOrMobile, RegSource regSource);
}
