package com.application.mrmason.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.application.mrmason.entity.SPWorkAssignment;
import com.application.mrmason.entity.SpWorkers;
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.SPWorkAssignmentRepository;
import com.application.mrmason.repository.SpWorkersRepo;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.security.AuthDetailsProvider;
import com.application.mrmason.service.SPWorkAssignmentService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class SPWorkAssignmentServiceImpl implements SPWorkAssignmentService {

	@Autowired
	private SPWorkAssignmentRepository repository;
	
	@Autowired
	SpWorkersRepo workerRepo;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	UserDAO userDAO;

	@Override
	public SPWorkAssignment createAssignment(SPWorkAssignment assignment,RegSource regSource) {
//	    String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
//
//	    User loginEmail = userDAO.findByEmailOne(loggedInUserEmail)
//	        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + loggedInUserEmail));

		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Collection<? extends GrantedAuthority> loggedInRole = AuthDetailsProvider.getLoggedRole();
		System.out.println("ROLE"+loggedInUserEmail);
		List<String> roleNames = loggedInRole.stream()
		        .map(GrantedAuthority::getAuthority)
		        .map(role -> role.replace("ROLE_", "")) // Remove "ROLE_" prefix
		        .collect(Collectors.toList());

		if (roleNames.equals("Developer")||roleNames.equals("Adm")) {
		    throw new ResourceNotFoundException("Role Developer not found in: " + roleNames);
		}
		UserType userType = UserType.valueOf(roleNames.get(0)); // Make sure roleNames is not empty
		User user = userDAO.findByEmailAndUserTypeAndRegSource(loggedInUserEmail, userType,regSource)
		    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + loggedInUserEmail));
		
	    List<SpWorkers> spWorkerList = workerRepo.findByWorkerId(assignment.getWorkerId());
	    if (spWorkerList.isEmpty()) {
	        throw new ResourceNotFoundException("Worker not found with ID: " + assignment.getWorkerId());
	    }

	    SpWorkers spWorker = spWorkerList.get(0);
	    assignment.setWorkerId(spWorker.getWorkerId());
	    assignment.setServicePersonId(spWorker.getServicePersonId());
	    assignment.setUpdatedBy(user.getBodSeqNo());
	    assignment.setUpdatedDate(new Date());
	    assignment.setCurrency("INR");

	    return repository.save(assignment);
	}

//	public List<SPWorkAssignment> getWorkers(String recId,String servicePersonId, String workerId, String updatedBy) {
//
//	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//	    CriteriaQuery<SPWorkAssignment> query = cb.createQuery(SPWorkAssignment.class);
//	    Root<SPWorkAssignment> root = query.from(SPWorkAssignment.class);
//
//	    List<Predicate> predicates = new ArrayList<>();
//
//	    if (recId != null && !recId.trim().isEmpty()) {
//	        predicates.add(cb.equal(root.get("recId"), recId));
//	    }
//	    if (servicePersonId != null && !servicePersonId.trim().isEmpty()) {
//	        predicates.add(cb.equal(root.get("servicePersonId"), servicePersonId));
//	    }
//	    if (workerId != null && !workerId.trim().isEmpty()) {
//	        predicates.add(cb.equal(root.get("workerId"), workerId));
//	    }
//	    if (updatedBy != null && !updatedBy.trim().isEmpty()) {
//	        predicates.add(cb.equal(root.get("updatedBy"), updatedBy));
//	    }
//
//	    query.select(root);
//	    if (!predicates.isEmpty()) {
//	        query.where(cb.and(predicates.toArray(new Predicate[0]))); // ✅ FIX: using AND instead of OR
//	    }
//
//	    return entityManager.createQuery(query).getResultList();
//	}

	@Override
	public SPWorkAssignment updateWorkAssignment(SPWorkAssignment updatedAssignment,RegSource regSource) {
		
//		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
//
//	    User loginEmail = userDAO.findByEmailOne(loggedInUserEmail)
//	        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + loggedInUserEmail));
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Collection<? extends GrantedAuthority> loggedInRole = AuthDetailsProvider.getLoggedRole();
		System.out.println("ROLE"+loggedInUserEmail);
		List<String> roleNames = loggedInRole.stream()
		        .map(GrantedAuthority::getAuthority)
		        .map(role -> role.replace("ROLE_", "")) // Remove "ROLE_" prefix
		        .collect(Collectors.toList());

		if (roleNames.equals("Developer")||roleNames.equals("Adm")) {
		    throw new ResourceNotFoundException("Role Developer not found in: " + roleNames);
		}
		UserType userType = UserType.valueOf(roleNames.get(0)); // Make sure roleNames is not empty
		User user = userDAO.findByEmailAndUserTypeAndRegSource(loggedInUserEmail, userType,regSource)
		    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + loggedInUserEmail));
	    SPWorkAssignment existing = repository.findById(updatedAssignment.getRecId())
	        .orElseThrow(() -> new EntityNotFoundException("Work assignment not found with recId: " + updatedAssignment.getRecId()));

	    // Update fields
//	    existing.setServicePersonId(updatedAssignment.getServicePersonId());
//	    existing.setWorkerId(updatedAssignment.getWorkerId());
	    existing.setDateOfWork(updatedAssignment.getDateOfWork());
	    existing.setUpdatedBy(user.getBodSeqNo());
	    existing.setUpdatedDate(new Date()); // Set current date/time
	    existing.setAmount(updatedAssignment.getAmount());
	    existing.setPaymentStatus(updatedAssignment.getPaymentStatus());
	    existing.setPaymentMethod(updatedAssignment.getPaymentMethod());
	    return repository.save(existing);
	}

	@Override
	public Page<SPWorkAssignment> getWorkers(String recId, String servicePersonId, String workerId, String updatedBy,
	        Pageable pageable) {

	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

	    // === Main query ===
	    CriteriaQuery<SPWorkAssignment> query = cb.createQuery(SPWorkAssignment.class);
	    Root<SPWorkAssignment> root = query.from(SPWorkAssignment.class);
	    List<Predicate> predicates = new ArrayList<>();

	    if (recId != null && !recId.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("recId"), recId));
	    }
	    if (servicePersonId != null && !servicePersonId.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("servicePersonId"), servicePersonId));
	    }
	    if (workerId != null && !workerId.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("workerId"), workerId));
	    }
	    if (updatedBy != null && !updatedBy.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("updatedBy"), updatedBy));
	    }

	    query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
	    TypedQuery<SPWorkAssignment> typedQuery = entityManager.createQuery(query);
	    typedQuery.setFirstResult((int) pageable.getOffset());
	    typedQuery.setMaxResults(pageable.getPageSize());

	    // === Count query ===
	    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
	    Root<SPWorkAssignment> countRoot = countQuery.from(SPWorkAssignment.class);
	    List<Predicate> countPredicates = new ArrayList<>();

	    if (recId != null && !recId.trim().isEmpty()) {
	        countPredicates.add(cb.equal(countRoot.get("recId"), recId));
	    }
	    if (servicePersonId != null && !servicePersonId.trim().isEmpty()) {
	        countPredicates.add(cb.equal(countRoot.get("servicePersonId"), servicePersonId));
	    }
	    if (workerId != null && !workerId.trim().isEmpty()) {
	        countPredicates.add(cb.equal(countRoot.get("workerId"), workerId));
	    }
	    if (updatedBy != null && !updatedBy.trim().isEmpty()) {
	        countPredicates.add(cb.equal(countRoot.get("updatedBy"), updatedBy));
	    }

	    countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
	    Long total = entityManager.createQuery(countQuery).getSingleResult();

	    return new PageImpl<>(typedQuery.getResultList(), pageable, total);
	}




}
