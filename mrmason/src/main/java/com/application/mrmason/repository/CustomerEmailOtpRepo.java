package com.application.mrmason.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.CustomerEmailOtp;
import com.application.mrmason.entity.ServicePersonLogin;
import com.application.mrmason.enums.RegSource;

@Repository
public interface CustomerEmailOtpRepo extends JpaRepository<CustomerEmailOtp,Long>{

	CustomerEmailOtp findByEmail(String email);
	
	CustomerEmailOtp findByEmailAndRegSource(String email,RegSource regSource);

	CustomerEmailOtp findByEmailAndRegSourceAndOtp(String email,RegSource regSource, String enteredOtp);
	
	@Query("SELECT c FROM CustomerEmailOtp c WHERE c.email = :email AND c.regSource = :regSource")
	Optional<CustomerEmailOtp> findByEmailAndRegSources(@Param("email") String email,
	                                                  @Param("regSource") RegSource regSource);

	CustomerEmailOtp findByEmailAndOtpAndRegSource(String email, String enteredOtp, RegSource regSource);

}
