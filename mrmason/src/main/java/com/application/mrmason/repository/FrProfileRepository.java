package com.application.mrmason.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.FrProfile;

@Repository
public interface FrProfileRepository extends JpaRepository<FrProfile, String> {
	@Query("SELECT f FROM FrProfile f WHERE f.frUserId = :frUserId")
	Optional<FrProfile> findByFrUserId(@Param("frUserId") String frUserId);

	@Query("SELECT f FROM FrProfile f WHERE f.frUserId = :frUserId")
	List<FrProfile> findAllByFrUserId(@Param("frUserId") String frUserId);

	@Query("SELECT f FROM FrProfile f WHERE LOWER(f.primarySkill) LIKE LOWER(CONCAT('%', :skill, '%'))")
	List<FrProfile> findByPrimarySkillLikeIgnoreCase(@Param("skill") String skill);

	@Query("SELECT f FROM FrProfile f WHERE LOWER(f.secondarySkill) LIKE LOWER(CONCAT('%', :skill, '%'))")
	List<FrProfile> findBySecondarySkillLikeIgnoreCase(@Param("skill") String skill);

}
