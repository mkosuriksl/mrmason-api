package com.application.mrmason.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import com.application.mrmason.entity.AdminSpQualification;
import com.application.mrmason.repository.AdminSpQualificationRepo;
import com.application.mrmason.service.AdminSpQualificationService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class AdminSpQualificationServiceImpl implements AdminSpQualificationService {

	@Autowired
	AdminSpQualificationRepo repo;

	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public AdminSpQualification addQualification(AdminSpQualification qualification) {

		qualification.setCourseId(generateCourseId(qualification.getEducationId(), qualification.getBranchId()));
		Optional<AdminSpQualification> courseIdExists = repo.findById(qualification.getCourseId());
		if (!courseIdExists.isPresent()) {
			repo.save(qualification);
			return qualification;
		}
		return null;

	}

	private String generateCourseId(String educationId, String branchId) {
		return educationId + "_" + branchId;
	}

	
	@Override
	public AdminSpQualification update(AdminSpQualification update) {

		Optional<AdminSpQualification> courseIdExists = repo.findById(update.getCourseId());
		if (courseIdExists.isPresent()) {
			courseIdExists.get().setName(update.getName());
			courseIdExists.get().setBranchName(update.getBranchName());
			return repo.save(courseIdExists.get());
			
		} else {
			return null;

		}
	}
	
	
	@Override
	public Page<AdminSpQualification> getQualification(String courseId, String educationId, String name,
			String branchId, String branchName,Pageable pageable) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<AdminSpQualification> query = cb.createQuery(AdminSpQualification.class);
		Root<AdminSpQualification> root = query.from(AdminSpQualification.class);
		List<Predicate> predicates = new ArrayList<>();

		if (courseId != null) {
			predicates.add(cb.equal(root.get("courseId"), courseId));
		}
		if (educationId != null) {
			predicates.add(cb.equal(root.get("educationId"), educationId));
		}
		if (name != null) {
			predicates.add(cb.equal(root.get("name"), name));
		}
		if (branchId != null) {
			predicates.add(cb.equal(root.get("branchId"), branchId));
		}
		if (branchName != null) {
			predicates.add(cb.equal(root.get("branchName"),branchName));
		}

		query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<AdminSpQualification> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult((int) pageable.getOffset());
		typedQuery.setMaxResults(pageable.getPageSize());

		// Count query
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<AdminSpQualification> countRoot = countQuery.from(AdminSpQualification.class);
		List<Predicate> countPredicates = new ArrayList<>();
		
		if (courseId != null) {
			countPredicates.add(cb.equal(countRoot.get("courseId"), courseId));
		}
		if (educationId != null) {
			countPredicates.add(cb.equal(countRoot.get("educationId"), educationId));
		}
		if (name != null) {
			countPredicates.add(cb.equal(countRoot.get("name"), name));
		}
		if (branchId != null) {
			countPredicates.add(cb.equal(countRoot.get("branchId"), branchId));
		}
		if (branchName != null) {
			countPredicates.add(cb.equal(countRoot.get("branchName"),branchName));
		}
		
		countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
		Long total = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(typedQuery.getResultList(), pageable, total);
	}
	
	
	@Override
	 public List<AdminSpQualification> getAllQualifications() {
	        return repo.findAll();
	    }
}