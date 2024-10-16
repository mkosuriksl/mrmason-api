package com.application.mrmason.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.AdminSms;

@Repository
public interface AdminSmsRepo extends JpaRepository<AdminSms, Long> {

	@Query("SELECT a FROM AdminSms a WHERE a.active = true")
	Optional<AdminSms> findByActive();
	
	@Query("SELECT a FROM AdminSms a WHERE a.active = true")
	List<AdminSms> findAllByActive();
}
