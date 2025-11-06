package com.application.mrmason.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.FrAvailable;

@Repository
public interface FrAvaiableRepository extends JpaRepository<FrAvailable, String> {
	@Query("SELECT f FROM FrAvailable f WHERE f.frUserId = :frUserId")
	Optional<FrAvailable> findByFrUserId(@Param("frUserId") String frUserId);

	@Query("SELECT f FROM FrAvailable f WHERE f.frUserId = :frUserId")
	List<FrAvailable> findAllByFrUserId(@Param("frUserId") String frUserId);

}
