package com.application.mrmason.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.FrLogin;
import com.application.mrmason.enums.RegSource;

@Repository
public interface FrLoginRepo extends JpaRepository<FrLogin, Long>{

	Optional<FrLogin> findByFrEmailAndRegSource(String frEmail, RegSource regSource);

	Optional<FrLogin> findByFrMobileAndRegSource(String frMobile, RegSource regSource);

//	FrLogin findByFrEmailOrFrMobile(String frEmail, String frMobile);

	Optional<FrLogin> findByFrEmailOrFrMobileAndRegSource(String frEmail, String frMobile, RegSource regSource);

    Optional<FrLogin> findByFrEmailOrFrMobile(String email, String mobile);
    
    Optional<FrLogin> findByFrEmailAndRegSource(String email, String regSource);

    Optional<FrLogin> findByFrMobileAndRegSource(String mobile, String regSource);


}
