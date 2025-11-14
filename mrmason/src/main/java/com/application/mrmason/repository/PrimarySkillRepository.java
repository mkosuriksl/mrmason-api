package com.application.mrmason.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.PrimarySkill;

@Repository
public interface PrimarySkillRepository extends JpaRepository<PrimarySkill, String> {

	void deleteByFrUserId(String frUserId);

	List<PrimarySkill> findByFrUserId(String frUserId);

	Page<PrimarySkill> findAll(Specification<PrimarySkill> primarySpec, Pageable pageable);

}