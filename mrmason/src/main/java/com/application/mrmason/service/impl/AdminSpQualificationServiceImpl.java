package com.application.mrmason.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.mrmason.entity.AdminSpQualification;
import com.application.mrmason.repository.AdminSpQualificationRepo;
import com.application.mrmason.service.AdminSpQualificationService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
	public List<AdminSpQualification> getQualification(String courseId, String educationId, String name,
			String branchId, String branchName) {
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

		query.where(predicates.toArray(new Predicate[0]));

		return entityManager.createQuery(query).getResultList();
	}
	
	
	@Override
	 public List<AdminSpQualification> getAllQualifications() {
	        return repo.findAll();
	    }
}