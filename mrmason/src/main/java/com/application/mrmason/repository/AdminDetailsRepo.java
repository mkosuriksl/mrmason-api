package com.application.mrmason.repository;



import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.AdminDetails;
import com.application.mrmason.entity.CustomerRegistration;
import com.application.mrmason.entity.UserType;
@Repository
public interface AdminDetailsRepo extends JpaRepository<AdminDetails,Long >{
	AdminDetails findByEmailOrMobile(String email,String mobile);

//	List<AdminDetails>  findByAdminType(String adminType);
	AdminDetails findByEmail(String email);
	AdminDetails findByMobile(String mobile);

	Optional<AdminDetails> findByEmailAndUserType(String loggedInUserEmail, UserType userType);
	@Query("SELECT a FROM AdminDetails a WHERE a.email = :email")
	Optional<AdminDetails> findByEmailDetails(@Param("email") String email);
}
