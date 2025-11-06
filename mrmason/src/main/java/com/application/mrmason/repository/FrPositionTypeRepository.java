package com.application.mrmason.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.FrPositionType;

@Repository
public interface FrPositionTypeRepository extends JpaRepository<FrPositionType, String> {
	@Query("SELECT f FROM FrPositionType f WHERE f.frUserId = :frUserId")
	Optional<FrPositionType> findByFrUserId(@Param("frUserId") String frUserId);

}
