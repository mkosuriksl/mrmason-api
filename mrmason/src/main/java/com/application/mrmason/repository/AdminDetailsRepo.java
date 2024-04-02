package com.application.mrmason.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.AdminDetails;
@Repository
public interface AdminDetailsRepo extends JpaRepository<AdminDetails,Long >{
	AdminDetails findByEmailOrMobile(String email,String mobile);
	List<AdminDetails>  findByAdminType(String adminType);
	
}
