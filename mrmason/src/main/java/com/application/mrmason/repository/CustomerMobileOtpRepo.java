package com.application.mrmason.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.CustomerMobileOtp;
import com.application.mrmason.enums.RegSource;
@Repository
public interface CustomerMobileOtpRepo extends JpaRepository<CustomerMobileOtp, Long> {
	CustomerMobileOtp findByMobileNumAndRegSource(String mobile,RegSource regSource);

	@Query("SELECT c FROM CustomerMobileOtp c WHERE c.mobileNum = :mobile AND c.regSource = :regSource")
	Optional<CustomerMobileOtp> findByMobileNumAndRegSources(@Param("mobile") String mobile,
	                                                        @Param("regSource") RegSource regSource);
	CustomerMobileOtp findByMobileNumAndOtpAndRegSource(String email, String enteredOtp, RegSource regSource);

}
