package com.application.mrmason.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.application.mrmason.entity.AdminDetails;
import com.application.mrmason.entity.SPWAStatus;
import com.application.mrmason.entity.SPWorkAssignment;
import com.application.mrmason.entity.ServiceRequest;
import com.application.mrmason.entity.ServiceRequestPaintQuotation;
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.AdminDetailsRepo;
import com.application.mrmason.repository.ServiceRequestPaintQuotationRepository;
import com.application.mrmason.repository.ServiceRequestRepo;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.security.AuthDetailsProvider;
import com.application.mrmason.service.ServiceRequestPaintQuotationService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class ServiceRequestPaintQuotationServiceImpl implements ServiceRequestPaintQuotationService {

	@Autowired
	public AdminDetailsRepo adminRepo;

	@Autowired
	UserDAO userDAO;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private ServiceRequestRepo serviceRequestRepo;

	@Autowired
	private ServiceRequestPaintQuotationRepository serviceRequestPaintQuotationRepository;

	@Override
	public List<ServiceRequestPaintQuotation> createServiceRequestPaintQuotationService(
		    String requestId, List<ServiceRequestPaintQuotation> dtoList, RegSource regSource) {

		    UserInfo userInfo = getLoggedInUserInfo(regSource);
		    ServiceRequest serviceRequest = serviceRequestRepo.findByRequestId(requestId);

		    if (serviceRequest == null) {
		        throw new RuntimeException("Service request not found with ID: " + requestId);
		    }

		    List<ServiceRequestPaintQuotation> savedQuotations = new ArrayList<>();
		    int counter = 1;

		    for (ServiceRequestPaintQuotation dto : dtoList) {
		        ServiceRequestPaintQuotation sRPQ = new ServiceRequestPaintQuotation();
		        sRPQ.setRequestId(requestId);

		        String lineId = requestId + "_" + String.format("%04d", counter++);
		        sRPQ.setRequestLineId(lineId);

		        sRPQ.setRequestLineIdDescription(dto.getRequestLineIdDescription());
		        sRPQ.setAreasInSqft(dto.getAreasInSqft());
		        sRPQ.setQuotationAmount(dto.getQuotationAmount());
		        sRPQ.setQuotedDate(dto.getQuotedDate());
		        sRPQ.setStatus(SPWAStatus.NEW);
		        sRPQ.setNoOfDays(dto.getNoOfDays());
		        sRPQ.setNoOfResources(dto.getNoOfResources());
		        sRPQ.setSpId(userInfo.userId);
		        sRPQ.setUpdatedBy(userInfo.userId);
		        sRPQ.setUpdatedDate(new Date());

		        ServiceRequestPaintQuotation saved = serviceRequestPaintQuotationRepository.save(sRPQ);
		        savedQuotations.add(saved);
		    }

		    return savedQuotations;
		}


	private static class UserInfo {

		String userId;

		UserInfo(String userId) {
			this.userId = userId;
		}
	}

	private UserInfo getLoggedInUserInfo(RegSource regSource) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Collection<? extends GrantedAuthority> loggedInRole = AuthDetailsProvider.getLoggedRole();

		List<String> roleNames = loggedInRole.stream().map(GrantedAuthority::getAuthority)
				.map(role -> role.replace("ROLE_", "")).collect(Collectors.toList());

		if (roleNames.equals("Developer")) {
			throw new ResourceNotFoundException("Restricted role: " + roleNames);
		}

		UserType userType = UserType.valueOf(roleNames.get(0));
		String userId;

		if (userType == UserType.Adm) {
			AdminDetails admin = adminRepo.findByEmailAndUserType(loggedInUserEmail, userType)
					.orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + loggedInUserEmail));
			userId = admin.getEmail(); // or any other logic you want
		} else {
			User user = userDAO.findByEmailAndUserTypeAndRegSource(loggedInUserEmail, userType, regSource)
					.orElseThrow(() -> new ResourceNotFoundException("User not found: " + loggedInUserEmail));
			userId = user.getBodSeqNo();
		}

		return new UserInfo(userId);
	}
	
	@Override
	public Page<ServiceRequestPaintQuotation> getServiceRequestPaintQuotationService(
			String requestLineId, String requestLineIdDescription, String requestId,
			Integer quotationAmount,String status,String spId,Pageable pageable) {

	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

	    // === Main query ===
	    CriteriaQuery<ServiceRequestPaintQuotation> query = cb.createQuery(ServiceRequestPaintQuotation.class);
	    Root<ServiceRequestPaintQuotation> root = query.from(ServiceRequestPaintQuotation.class);
	    List<Predicate> predicates = new ArrayList<>();

	    if (requestLineId != null && !requestLineId.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("requestLineId"), requestLineId));
	    }
	    if (requestLineIdDescription != null && !requestLineIdDescription.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("requestLineIdDescription"), requestLineIdDescription));
	    }
	    if (requestId != null && !requestId.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("requestId"), requestId));
	    }
	    if (quotationAmount != null ) {
	        predicates.add(cb.equal(root.get("quotationAmount"), quotationAmount));
	    }
	    if (status != null && !status.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("status"), status));
	    }
	    if (spId != null && !spId.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("spId"), spId));
	    }
		
	    query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
	    TypedQuery<ServiceRequestPaintQuotation> typedQuery = entityManager.createQuery(query);
	    typedQuery.setFirstResult((int) pageable.getOffset());
	    typedQuery.setMaxResults(pageable.getPageSize());

	    // === Count query ===
	    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
	    Root<ServiceRequestPaintQuotation> countRoot = countQuery.from(ServiceRequestPaintQuotation.class);
	    List<Predicate> countPredicates = new ArrayList<>();

	    if (requestLineId != null && !requestLineId.trim().isEmpty()) {
	    	countPredicates.add(cb.equal(countRoot.get("requestLineId"), requestLineId));
	    }
	    if (requestLineIdDescription != null && !requestLineIdDescription.trim().isEmpty()) {
	    	countPredicates.add(cb.equal(countRoot.get("requestLineIdDescription"), requestLineIdDescription));
	    }
	    if (requestId != null && !requestId.trim().isEmpty()) {
	    	countPredicates.add(cb.equal(countRoot.get("requestId"), requestId));
	    }
	    if (quotationAmount != null ) {
	    	countPredicates.add(cb.equal(countRoot.get("quotationAmount"), quotationAmount));
	    }
	    if (status != null && !status.trim().isEmpty()) {
	    	countPredicates.add(cb.equal(countRoot.get("status"), status));
	    }
	    if (spId != null && !spId.trim().isEmpty()) {
	    	countPredicates.add(cb.equal(countRoot.get("spId"), spId));
	    }
	    countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
	    Long total = entityManager.createQuery(countQuery).getSingleResult();

	    return new PageImpl<>(typedQuery.getResultList(), pageable, total);
	}

}
