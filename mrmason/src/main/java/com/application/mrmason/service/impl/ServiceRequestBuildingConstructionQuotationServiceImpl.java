package com.application.mrmason.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
import com.application.mrmason.entity.SiteMeasurement;
import com.application.mrmason.entity.ServiceRequest;
import com.application.mrmason.entity.ServiceRequestBuildingConstructionQuotation;
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.AdminDetailsRepo;
import com.application.mrmason.repository.ServiceRequestBuildingConstructionQuotationRepository;
import com.application.mrmason.repository.ServiceRequestRepo;
import com.application.mrmason.repository.SiteMeasurementRepository;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.security.AuthDetailsProvider;
import com.application.mrmason.service.ServiceRequestBuildingConstructionQuotationService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class ServiceRequestBuildingConstructionQuotationServiceImpl
		implements ServiceRequestBuildingConstructionQuotationService {

	@Autowired
	public AdminDetailsRepo adminRepo;

	@Autowired
	UserDAO userDAO;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private SiteMeasurementRepository serviceRequestRepo;

	@Autowired
	private ServiceRequestBuildingConstructionQuotationRepository buildingConstructionQuotationRepository ;

	@Override
	public List<ServiceRequestBuildingConstructionQuotation> createServiceRequestBuildingConstructionQuotation(
			String requestId, List<ServiceRequestBuildingConstructionQuotation> dtoList, RegSource regSource) {
		UserInfo userInfo = getLoggedInUserInfo(regSource);
		SiteMeasurement serviceRequest = serviceRequestRepo.findByServiceRequestId(requestId);

		if (serviceRequest == null) {
			throw new RuntimeException("Service request not found with ID: " + requestId);
		}

		// Step 1: Fetch existing line IDs for this requestId
		List<String> existingLineIds = buildingConstructionQuotationRepository.findByRequestId(requestId).stream()
				.map(ServiceRequestBuildingConstructionQuotation::getRequestLineId).collect(Collectors.toList());

		// Step 2: Extract the highest counter
		int maxCounter = existingLineIds.stream().map(id -> id.substring(id.lastIndexOf("_") + 1))
				.mapToInt(Integer::parseInt).max().orElse(0); // If no entries exist, start at 0

		List<ServiceRequestBuildingConstructionQuotation> savedQuotations = new ArrayList<>();

		for (ServiceRequestBuildingConstructionQuotation dto : dtoList) {
			// Generate next lineId
			int nextCounter = ++maxCounter;
			String lineId = requestId + "_" + String.format("%04d", nextCounter);

			// Create new entry
			ServiceRequestBuildingConstructionQuotation sRPQ = new ServiceRequestBuildingConstructionQuotation();
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

			ServiceRequestBuildingConstructionQuotation saved = buildingConstructionQuotationRepository.save(sRPQ);
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
	public Page<ServiceRequestBuildingConstructionQuotation> getServiceRequestBuildingConstructionQuotation(
			String requestLineId, String requestLineIdDescription, String requestId, Integer quotationAmount,
			String status, String spId, Pageable pageable) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		// === Main query ===
		CriteriaQuery<ServiceRequestBuildingConstructionQuotation> query = cb
				.createQuery(ServiceRequestBuildingConstructionQuotation.class);
		Root<ServiceRequestBuildingConstructionQuotation> root = query.from(ServiceRequestBuildingConstructionQuotation.class);
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
		if (quotationAmount != null) {
			predicates.add(cb.equal(root.get("quotationAmount"), quotationAmount));
		}
		if (status != null && !status.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("status"), status));
		}
		if (spId != null && !spId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("spId"), spId));
		}

		query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<ServiceRequestBuildingConstructionQuotation> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult((int) pageable.getOffset());
		typedQuery.setMaxResults(pageable.getPageSize());

		// === Count query ===
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<ServiceRequestBuildingConstructionQuotation> countRoot = countQuery.from(ServiceRequestBuildingConstructionQuotation.class);
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
		if (quotationAmount != null) {
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
	public List<ServiceRequestBuildingConstructionQuotation> updateServiceRequestBuildingConstructionQuotation(
			String requestId, List<ServiceRequestBuildingConstructionQuotation> dtoList, RegSource regSource) {
		UserInfo userInfo = getLoggedInUserInfo(regSource);
		SiteMeasurement serviceRequest = serviceRequestRepo.findByServiceRequestId(requestId);

		if (serviceRequest == null) {
			throw new RuntimeException("Service request not found with ID: " + requestId);
		}

		// Step 1: Get existing records and map them by requestLineId
		Map<String, ServiceRequestBuildingConstructionQuotation> existingMap = buildingConstructionQuotationRepository
				.findByRequestId(requestId).stream()
				.collect(Collectors.toMap(ServiceRequestBuildingConstructionQuotation::getRequestLineId, Function.identity()));

		List<ServiceRequestBuildingConstructionQuotation> updatedQuotations = new ArrayList<>();

		for (ServiceRequestBuildingConstructionQuotation dto : dtoList) {
			ServiceRequestBuildingConstructionQuotation existing = existingMap.get(dto.getRequestLineId());

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

				ServiceRequestBuildingConstructionQuotation saved = buildingConstructionQuotationRepository.save(existing);
				updatedQuotations.add(saved);
			}
			// else: skip as it's not an existing record
		}

		return updatedQuotations;
	}

}
