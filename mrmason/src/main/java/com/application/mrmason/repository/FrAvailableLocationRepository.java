package com.application.mrmason.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.FrAvaiableLocation;

@Repository
public interface FrAvailableLocationRepository extends JpaRepository<FrAvaiableLocation, String> {
	@Query("SELECT f FROM FrAvaiableLocation f WHERE f.frUserId = :frUserId")
	Optional<FrAvaiableLocation> findByFrUserId(@Param("frUserId") String frUserId);

}
