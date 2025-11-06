package com.application.mrmason.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.FrServiceRole;

@Repository
public interface FrServiceRoleRepository extends JpaRepository<FrServiceRole, String> {
	@Query("SELECT f FROM FrServiceRole f WHERE f.frUserId = :frUserId")
	Optional<FrServiceRole> findByFrUserId(@Param("frUserId") String frUserId);

	@Query("SELECT f FROM FrServiceRole f WHERE f.frUserId = :frUserId")
	List<FrServiceRole> findAllByFrUserId(@Param("frUserId") String frUserId);

	@Query("SELECT f FROM FrServiceRole f WHERE LOWER(f.training) LIKE LOWER(CONCAT('%', :training, '%'))")
    List<FrServiceRole> findByTrainingContainingIgnoreCase(@Param("training") String training);
	
}
