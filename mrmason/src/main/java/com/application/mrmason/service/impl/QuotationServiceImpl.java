package com.application.mrmason.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.application.mrmason.entity.QuotationEntity;
import com.application.mrmason.entity.SiteMeasurement;
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.QuotationRepository;
import com.application.mrmason.repository.SiteMeasurementRepository;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.security.AuthDetailsProvider;
import com.application.mrmason.service.QuotationService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class QuotationServiceImpl implements QuotationService {

	@Autowired
	private QuotationRepository repository;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	UserDAO userDAO;
	
    @Autowired
    private SiteMeasurementRepository siteRepository;

	@Override
	public QuotationEntity createQuotation(QuotationEntity quotationEntity,RegSource regSource) {
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
		quotationEntity.setUpdatedBy(user.getBodSeqNo());
		SiteMeasurement siteMeasurement=siteRepository.findByServiceRequestId(quotationEntity.getReqId());
		quotationEntity.setReqId(siteMeasurement.getServiceRequestId());
		quotationEntity.setUpdatedDate(new Date());
		quotationEntity.setServicePersonId(user.getBodSeqNo());
		return repository.save(quotationEntity);
	}

	@Override
	public QuotationEntity updateQuotation(QuotationEntity updatedQuotation,RegSource regSource) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Collection<? extends GrantedAuthority> loggedInRole = AuthDetailsProvider.getLoggedRole();
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
	    QuotationEntity existing = repository.findById(updatedQuotation.getReqId())
	        .orElseThrow(() -> new EntityNotFoundException("Work assignment not found with recId: " + updatedQuotation.getReqId()));

	    existing.setUpdatedBy(user.getBodSeqNo());
	    existing.setUpdatedDate(new Date()); // Set current date/time
	    existing.setStatus(updatedQuotation.getStatus());
	    existing.setQuotedAmount(updatedQuotation.getQuotedAmount());
	    return repository.save(existing);
	}

	@Override
	public List<QuotationEntity> getQuotation(String reqId, String customerId, String servicePersonId, String updatedBy) {
		    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		    CriteriaQuery<QuotationEntity> query = cb.createQuery(QuotationEntity.class);
		    Root<QuotationEntity> root = query.from(QuotationEntity.class);

		    List<Predicate> predicates = new ArrayList<>();

		    if (reqId != null && !reqId.trim().isEmpty()) {
		        predicates.add(cb.equal(root.get("reqId"), reqId));
		    }
		    if (customerId != null && !customerId.trim().isEmpty()) {
		        predicates.add(cb.equal(root.get("customerId"), customerId));
		    }
		    if (servicePersonId != null && !servicePersonId.trim().isEmpty()) {
		        predicates.add(cb.equal(root.get("servicePersonId"), servicePersonId));
		    }   
		    if (updatedBy != null && !updatedBy.trim().isEmpty()) {
		        predicates.add(cb.equal(root.get("updatedBy"), updatedBy));
		    }
		    query.select(root);
		    if (!predicates.isEmpty()) {
		        query.where(cb.and(predicates.toArray(new Predicate[0]))); // ✅ FIX: using AND instead of OR
		    }

		    return entityManager.createQuery(query).getResultList();

	}

//	@Override
//	public SPWorkAssignment createAssignment(SPWorkAssignment assignment) {
//	    String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
//
//	    User loginEmail = userDAO.findByEmailOne(loggedInUserEmail)
//	        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + loggedInUserEmail));
//
//	    List<SpWorkers> spWorkerList = workerRepo.findByWorkerId(assignment.getWorkerId());
//	    if (spWorkerList.isEmpty()) {
//	        throw new ResourceNotFoundException("Worker not found with ID: " + assignment.getWorkerId());
//	    }
//
//	    SpWorkers spWorker = spWorkerList.get(0);
//	    assignment.setWorkerId(spWorker.getWorkerId());
//	    assignment.setServicePersonId(spWorker.getServicePersonId());
//	    assignment.setUpdatedBy(loginEmail.getBodSeqNo());
//	    assignment.setUpdatedDate(new Date());
//	    assignment.setCurrency("INR");
//
//	    return repository.save(assignment);
//	}
//
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
//
//	@Override
//	public SPWorkAssignment updateWorkAssignment(SPWorkAssignment updatedAssignment) {
//		
//		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
//
//	    User loginEmail = userDAO.findByEmailOne(loggedInUserEmail)
//	        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + loggedInUserEmail));
//	    SPWorkAssignment existing = repository.findById(updatedAssignment.getRecId())
//	        .orElseThrow(() -> new EntityNotFoundException("Work assignment not found with recId: " + updatedAssignment.getRecId()));
//
//	    // Update fields
////	    existing.setServicePersonId(updatedAssignment.getServicePersonId());
////	    existing.setWorkerId(updatedAssignment.getWorkerId());
//	    existing.setDateOfWork(updatedAssignment.getDateOfWork());
//	    existing.setUpdatedBy(loginEmail.getBodSeqNo());
//	    existing.setUpdatedDate(new Date()); // Set current date/time
//	    existing.setAmount(updatedAssignment.getAmount());
//	    existing.setPaymentStatus(updatedAssignment.getPaymentStatus());
//	    existing.setPaymentMethod(updatedAssignment.getPaymentMethod());
//	    return repository.save(existing);
//	}

}
