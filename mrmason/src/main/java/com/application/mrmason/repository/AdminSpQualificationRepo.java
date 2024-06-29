package com.application.mrmason.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.entity.AdminSpQualification;

@RestController
public interface AdminSpQualificationRepo extends JpaRepository<AdminSpQualification, String>{

}
