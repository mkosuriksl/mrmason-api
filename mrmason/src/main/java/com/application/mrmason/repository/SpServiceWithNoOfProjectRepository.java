package com.application.mrmason.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.application.mrmason.entity.SpServiceWithNoOfProject;

public interface SpServiceWithNoOfProjectRepository extends JpaRepository<SpServiceWithNoOfProject, String> {

	SpServiceWithNoOfProject findByUserServicesId(String userServicesId);
}
