package com.application.mrmason.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.SecondarySkill;

@Repository
public interface SecondarySkillRepository extends JpaRepository<SecondarySkill, String> {

	void deleteByFrUserId(String frUserId);

	List<SecondarySkill> findByFrUserId(String frUserId);

	Page<SecondarySkill> findAll(Specification<SecondarySkill> secondarySpec, Pageable pageable);}

