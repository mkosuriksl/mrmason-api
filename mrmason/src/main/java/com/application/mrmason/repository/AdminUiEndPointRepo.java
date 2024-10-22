package com.application.mrmason.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.AdminUiEndPoint;

@Repository
public interface AdminUiEndPointRepo extends JpaRepository<AdminUiEndPoint, String>{

	Optional<AdminUiEndPoint> findByUpdatedBy(String updatedBy);

	
}
