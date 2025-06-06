package com.application.mrmason.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.application.mrmason.entity.AdminDetails;
import com.application.mrmason.entity.SPWAStatus;
import com.application.mrmason.entity.ServiceRequest;
import com.application.mrmason.entity.ServiceRequestPaintQuotation;
import com.application.mrmason.entity.ServiceRequestPlumbingQuotation;
import com.application.mrmason.entity.ServiceRequestQuotation;
import com.application.mrmason.entity.SiteMeasurement;
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.AdminDetailsRepo;
import com.application.mrmason.repository.ServiceRequestPlumbingQuotationRepository;
import com.application.mrmason.repository.ServiceRequestQuotationRepository;
import com.application.mrmason.repository.ServiceRequestRepo;
import com.application.mrmason.repository.SiteMeasurementRepository;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.security.AuthDetailsProvider;
import com.application.mrmason.service.ServiceRequestPlumbingQuotationService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class ServiceRequestPlumbingQuotationServiceImpl implements ServiceRequestPlumbingQuotationService {

	@Autowired
	public AdminDetailsRepo adminRepo;

	@Autowired
	UserDAO userDAO;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private SiteMeasurementRepository serviceRequestRepo;

	@Autowired
	private ServiceRequestPlumbingQuotationRepository plumbingQuotationRepository;

	@Autowired
	ServiceRequestQuotationRepository serviceRequestQuotationAuditRepository;
	
	@Override
	public List<ServiceRequestPlumbingQuotation> createServiceRequestPlumbingQuotation(
			String requestId,List<ServiceRequestPlumbingQuotation> dtoList, RegSource regSource) {
		UserInfo userInfo = getLoggedInUserInfo(regSource);

		int maxCounter = plumbingQuotationRepository.findByRequestId(requestId).stream()
			    .map(ServiceRequestPlumbingQuotation::getRequestLineId)
			    .map(id -> id.substring(id.lastIndexOf("_") + 1))
			    .mapToInt(Integer::parseInt)
			    .max()
			    .orElse(0);

	    List<ServiceRequestPlumbingQuotation> savedQuotations = new ArrayList<>();

	    Integer totalQuotationAmount = 0;
	    for (ServiceRequestPlumbingQuotation dto : dtoList) {
	        // Generate next lineId
	        int nextCounter = ++maxCounter;
	        String lineId = requestId + "_" + String.format("%04d", nextCounter);

	        // Create new entry
	        ServiceRequestPlumbingQuotation sRPQ = new ServiceRequestPlumbingQuotation();
	        sRPQ.setRequestId(requestId);
	        sRPQ.setRequestLineId(lineId);
	        sRPQ.setRequestLineIdDescription(dto.getRequestLineIdDescription());
	        sRPQ.setAreasInSqft(dto.getAreasInSqft());
	        sRPQ.setQuotationAmount(dto.getQuotationAmount());
	        sRPQ.setQuotedDate(new Date());
	        sRPQ.setStatus(SPWAStatus.NEW);
	        sRPQ.setNoOfDays(dto.getNoOfDays());
	        sRPQ.setNoOfResources(dto.getNoOfResources());
	        sRPQ.setSpId(userInfo.userId);
	        sRPQ.setUpdatedBy(userInfo.userId);
	        sRPQ.setUpdatedDate(new Date());

	        ServiceRequestPlumbingQuotation saved = plumbingQuotationRepository.save(sRPQ);
	        savedQuotations.add(saved);
			totalQuotationAmount += dto.getQuotationAmount();

	    }
	    
	    Collection<ServiceRequestPlumbingQuotation> allQuotationsForRequest = plumbingQuotationRepository.findByRequestId(requestId);

	    Integer totalQuotationAmountFromDb = allQuotationsForRequest.stream()
	            .map(ServiceRequestPlumbingQuotation::getQuotationAmount)
	            .filter(Objects::nonNull)
	            .reduce(0, Integer::sum);

	    // ✅ Step 3: Update or insert into ServiceRequestQuotation header
	    List<ServiceRequestQuotation> existingAuditOpt = serviceRequestQuotationAuditRepository.findByRequestId(requestId);

	    ServiceRequestQuotation audit;
	    if (!existingAuditOpt.isEmpty()) {
	        // Update existing
	        audit = existingAuditOpt.get(0);
	        audit.setQuotationAmount(totalQuotationAmountFromDb);
	        audit.setUpdatedBy(userInfo.userId);
	        audit.setUpdatedDate(new Date());
	    } else {
	        // Create new
	        audit = new ServiceRequestQuotation();
	        audit.setRequestId(requestId);
	        audit.setQuotationAmount(totalQuotationAmountFromDb);
	        audit.setQuotedDate(new Date());
	        audit.setQuotatedBy(userInfo.userId);
	        audit.setStatus(SPWAStatus.NEW);
	        audit.setUpdatedBy(userInfo.userId);
	        audit.setUpdatedDate(new Date());
	    }
	    serviceRequestQuotationAuditRepository.save(audit);
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
	public Page<ServiceRequestPlumbingQuotation> getServiceRequestPlumbingQuotation(
			String requestLineId, String requestLineIdDescription, String requestId, Integer quotationAmount,
			String status, String spId, Pageable pageable) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

	    // === Main query ===
	    CriteriaQuery<ServiceRequestPlumbingQuotation> query = cb.createQuery(ServiceRequestPlumbingQuotation.class);
	    Root<ServiceRequestPlumbingQuotation> root = query.from(ServiceRequestPlumbingQuotation.class);
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
	    TypedQuery<ServiceRequestPlumbingQuotation> typedQuery = entityManager.createQuery(query);
	    typedQuery.setFirstResult((int) pageable.getOffset());
	    typedQuery.setMaxResults(pageable.getPageSize());

	    // === Count query ===
	    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
	    Root<ServiceRequestPlumbingQuotation> countRoot = countQuery.from(ServiceRequestPlumbingQuotation.class);
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

	@Override
	public List<ServiceRequestPlumbingQuotation> updateServiceRequestPlumbingQuotation(String requestId,
			List<ServiceRequestPlumbingQuotation> dtoList, RegSource regSource) {
		UserInfo userInfo = getLoggedInUserInfo(regSource);
//		SiteMeasurement serviceRequest = serviceRequestRepo.findByServiceRequestId(requestId);
//
//	    if (serviceRequest == null) {
//	        throw new RuntimeException("Service request not found with ID: " + requestId);
//	    }

	    // Step 1: Get existing records and map them by requestLineId
	    Map<String, ServiceRequestPlumbingQuotation> existingMap = plumbingQuotationRepository
	            .findByRequestId(requestId)
	            .stream()
	            .collect(Collectors.toMap(ServiceRequestPlumbingQuotation::getRequestLineId, Function.identity()));

	    List<ServiceRequestPlumbingQuotation> updatedQuotations = new ArrayList<>();

	    for (ServiceRequestPlumbingQuotation dto : dtoList) {
	    	String lineRequestIdPrefix = dto.getRequestLineId().split("_")[0];
	        if (!lineRequestIdPrefix.equals(requestId)) {
	            throw new IllegalArgumentException("Invalid requestLineId: " + dto.getRequestLineId() +
	                " does not match requestId: " + requestId);
	        }
	    	ServiceRequestPlumbingQuotation existing = existingMap.get(dto.getRequestLineId());

	        if (existing != null) {
	            existing.setRequestLineIdDescription(dto.getRequestLineIdDescription());
	            existing.setAreasInSqft(dto.getAreasInSqft());
	            existing.setQuotationAmount(dto.getQuotationAmount());
	            existing.setQuotedDate(new Date());
	            existing.setStatus(dto.getStatus()); // Optional: mark as updated
	            existing.setNoOfDays(dto.getNoOfDays());
	            existing.setNoOfResources(dto.getNoOfResources());
	            existing.setUpdatedBy(userInfo.userId);
	            existing.setUpdatedDate(new Date());

	            ServiceRequestPlumbingQuotation saved = plumbingQuotationRepository.save(existing);
	            updatedQuotations.add(saved);
	        }
	        // else: skip as it's not an existing record
	    }
	    Integer totalQuotationAmount = plumbingQuotationRepository.findByRequestId(requestId).stream()
	            .map(ServiceRequestPlumbingQuotation::getQuotationAmount)
	            .filter(Objects::nonNull)
	            .reduce(0, Integer::sum);

	    // Step 3: Update or create ServiceRequestQuotation header
	    List<ServiceRequestQuotation>  optionalHeader = serviceRequestQuotationAuditRepository.findByRequestIds(requestId);

	    ServiceRequestQuotation header;
	    SPWAStatus status = !dtoList.isEmpty() ? dtoList.get(0).getStatus() : null;

	    if (!optionalHeader.isEmpty()) {
	        header = optionalHeader.get(0);
	        header.setQuotationAmount(totalQuotationAmount);
	        header.setUpdatedBy(userInfo.userId);
	        header.setUpdatedDate(new Date());
	        header.setStatus(status);
	    } else {
	        header = new ServiceRequestQuotation();
	        header.setRequestId(requestId);
	        header.setQuotationAmount(totalQuotationAmount);
	        header.setQuotedDate(new Date());
	        header.setQuotatedBy(userInfo.userId);
	        header.setUpdatedBy(userInfo.userId);
	        header.setUpdatedDate(new Date());
	        header.setStatus(status);
	    }
	    serviceRequestQuotationAuditRepository.save(header);
	    return updatedQuotations;
	}

}
