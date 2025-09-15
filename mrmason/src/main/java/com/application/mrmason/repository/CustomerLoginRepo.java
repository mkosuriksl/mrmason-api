package com.application.mrmason.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.CustomerLogin;
import com.application.mrmason.enums.RegSource;
@Repository
public interface CustomerLoginRepo extends JpaRepository<CustomerLogin, Long>{
	@Query("SELECT c FROM CustomerLogin c WHERE (c.userEmail = :email OR c.userMobile = :phno) AND c.regSource = :regSource")
	CustomerLogin findByUserEmailOrUserMobileAndRegSource(@Param("email") String email,
	                                              @Param("phno") String phno,
	                                              @Param("regSource") RegSource regSource);
	CustomerLogin findByUserEmail(String email);
	CustomerLogin findByUserEmailAndRegSource(String email,RegSource regSource);

	CustomerLogin findByUserMobileAndRegSource(String mobile,RegSource regSource);
	
	CustomerLogin findByUserEmailIgnoreCaseAndUserPassword(String userEmail, String userPassword);
}
