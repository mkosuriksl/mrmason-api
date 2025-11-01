package com.application.mrmason.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.application.mrmason.entity.FrReg;
import com.application.mrmason.enums.RegSource;

public interface FrRegRepository extends JpaRepository<FrReg, String> {
    Optional<FrReg> findByFrEmail(String email);
    @Query("SELECT f FROM FrReg f WHERE f.frEmail = :email")
    FrReg findByFrEmails(@Param("email") String email);
    Optional<FrReg> findByFrMobile(String mobile);
	Optional<FrReg> findByFrUserId(String frUserid);
	Optional<FrReg> findByFrEmailOrFrMobileAndRegSource(String email, String mobile, RegSource regSource);
	Optional<FrReg> findByFrEmailAndRegSource(String email, RegSource regSource);
}

