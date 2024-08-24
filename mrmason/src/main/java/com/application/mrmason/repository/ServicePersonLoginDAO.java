package com.application.mrmason.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.ServicePersonLogin;
import com.application.mrmason.enums.RegSource;

@Repository
public interface ServicePersonLoginDAO extends JpaRepository<ServicePersonLogin, Long> {

	ServicePersonLogin findByEmail(String email);
	
	Optional<ServicePersonLogin> findByEmailAndRegSource(String email, RegSource regSource);

	ServicePersonLogin findByEmailOrMobile(String email, String mobile);

	ServicePersonLogin findByMobile(String mobile);

	Optional<ServicePersonLogin> findByMobileAndRegSource(String mobile,  RegSource regSource);

}
