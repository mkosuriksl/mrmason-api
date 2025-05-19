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

import com.application.mrmason.dto.UpdateSiteMeasurementStatusRequestDTO;
import com.application.mrmason.dto.UpdateSiteMeasurementStatusResponseDTO;
import com.application.mrmason.entity.AdminDetails;
import com.application.mrmason.entity.CustomerRegistration;
import com.application.mrmason.entity.SiteMeasurement;
import com.application.mrmason.entity.UpdateSiteMeasurementStatus;
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.AdminDetailsRepo;
import com.application.mrmason.repository.CustomerRegistrationRepo;
import com.application.mrmason.repository.SiteMeasurementRepository;
import com.application.mrmason.repository.UpdateSiteMeasurementStatusRepository;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.security.AuthDetailsProvider;
import com.application.mrmason.service.SiteMeasurementService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class SiteMeasurementServiceImpl implements SiteMeasurementService {

	@Autowired
	private SiteMeasurementRepository repository;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	CustomerRegistrationRepo repo;

	@Autowired
	public AdminDetailsRepo adminRepo;

	@Autowired
	UserDAO userDAO;
	
	@Autowired
	private UpdateSiteMeasurementStatusRepository updateSMSR;

	@Override
	public SiteMeasurement addSiteMeasurement(SiteMeasurement measurement, RegSource regSource) {
//    	String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
//    	CustomerRegistration login = repo.findByUserEmail(loggedInUserEmail);
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Collection<? extends GrantedAuthority> loggedInRole = AuthDetailsProvider.getLoggedRole();

		List<String> roleNames = loggedInRole.stream().map(GrantedAuthority::getAuthority)
				.map(role -> role.replace("ROLE_", "")).collect(Collectors.toList());

		if (roleNames.equals("Developer")) {
			throw new ResourceNotFoundException("Restricted role: " + roleNames);
		}

		UserType userType = UserType.valueOf(roleNames.get(0));

		// User identity variables
		String userId;

		if (userType == UserType.EC) {
			// EC User
			CustomerRegistration customer = repo.findByUserEmailAndUserType(loggedInUserEmail, userType)
					.orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + loggedInUserEmail));

			userId = customer.getUserid();

		} else if (userType == UserType.Adm) {
			// Admin User
			AdminDetails admin = adminRepo.findByEmailAndUserType(loggedInUserEmail, userType)
					.orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + loggedInUserEmail));

			userId = admin.getEmail();// or any other logic you want to follow

		} else {
			// Other user types
			User user = userDAO.findByEmailAndUserTypeAndRegSource(loggedInUserEmail, userType, regSource)
					.orElseThrow(() -> new ResourceNotFoundException("User not found: " + loggedInUserEmail));

			userId = user.getBodSeqNo(); // modify if needed
		}

		measurement.setUpdatedBy(userId);
		measurement.setUpdatedDate(new Date());
		measurement.setRequestDate(new Date());
		measurement.setStatus("NEW");
		return repository.save(measurement);
	}

	@Override
	public SiteMeasurement updateSiteMeasurement(SiteMeasurement measurement, RegSource regSource) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Collection<? extends GrantedAuthority> loggedInRole = AuthDetailsProvider.getLoggedRole();

		List<String> roleNames = loggedInRole.stream().map(GrantedAuthority::getAuthority)
				.map(role -> role.replace("ROLE_", "")).collect(Collectors.toList());

		if (roleNames.equals("Developer")) {
			throw new ResourceNotFoundException("Restricted role: " + roleNames);
		}

		UserType userType = UserType.valueOf(roleNames.get(0));

		// User identity variables
		String userId;

		if (userType == UserType.EC) {
			// EC User
			CustomerRegistration customer = repo.findByUserEmailAndUserType(loggedInUserEmail, userType)
					.orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + loggedInUserEmail));

			userId = customer.getUserid();

		} else if (userType == UserType.Adm) {
			// Admin User
			AdminDetails admin = adminRepo.findByEmailAndUserType(loggedInUserEmail, userType)
					.orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + loggedInUserEmail));

			userId = admin.getEmail();// or any other logic you want to follow

		} else {
			// Other user types
			User user = userDAO.findByEmailAndUserTypeAndRegSource(loggedInUserEmail, userType, regSource)
					.orElseThrow(() -> new ResourceNotFoundException("User not found: " + loggedInUserEmail));

			userId = user.getBodSeqNo(); // modify if needed
		}

		SiteMeasurement existingMeasurement = repository.findByServiceRequestId(measurement.getServiceRequestId());
		if (existingMeasurement != null) {
			existingMeasurement.setEastSiteLength(measurement.getEastSiteLength());
			existingMeasurement.setWestSiteLength(measurement.getWestSiteLength());
			existingMeasurement.setSouthSiteLength(measurement.getSouthSiteLength());
			existingMeasurement.setNorthSiteLength(measurement.getNorthSiteLength());
			existingMeasurement.setLocation(measurement.getLocation());
			existingMeasurement.setExpectedBedRooms(measurement.getExpectedBedRooms());
			existingMeasurement.setExpectedAttachedBathRooms(measurement.getExpectedAttachedBathRooms());
			existingMeasurement.setExpectedAdditionalBathRooms(measurement.getExpectedAdditionalBathRooms());
			existingMeasurement.setExpectedStartDate(measurement.getExpectedStartDate());
			existingMeasurement.setCustomerId(measurement.getCustomerId());
			existingMeasurement.setUserId(measurement.getUserId());
			existingMeasurement.setUpdatedBy(userId);
			existingMeasurement.setNoOfFloors(measurement.getNoOfFloors());
			existingMeasurement.setBuildingType(measurement.getBuildingType());
			return repository.save(existingMeasurement);
		}
		return null;
	}

//    @Override
//	public List<SiteMeasurement> getSiteMeasurement(String serviceRequestId, String eastSiteLegth, String location) {
//
//	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//	    CriteriaQuery<SiteMeasurement> query = cb.createQuery(SiteMeasurement.class);
//	    Root<SiteMeasurement> root = query.from(SiteMeasurement.class);
//
//	    List<Predicate> predicates = new ArrayList<>();
//
//	    if (serviceRequestId != null && !serviceRequestId.trim().isEmpty()) {
//	        predicates.add(cb.equal(root.get("serviceRequestId"), serviceRequestId));
//	    }
//	    if (eastSiteLegth != null && !eastSiteLegth.trim().isEmpty()) {
//	        predicates.add(cb.equal(root.get("eastSiteLegth"), eastSiteLegth));
//	    }
//	    if (location != null && !location.trim().isEmpty()) {
//	        predicates.add(cb.equal(root.get("location"), location));
//	    }
//
//	    query.select(root);
//	    if (!predicates.isEmpty()) {
//	        query.where(cb.and(predicates.toArray(new Predicate[0]))); // ✅ FIX: using AND instead of OR
//	    }
//
//	    return entityManager.createQuery(query).getResultList();
//    }
//    @Override
//    public Page<SiteMeasurement> getSiteMeasurement(String serviceRequestId, String eastSiteLegth, String location,String userId,String updatedBy, Pageable pageable) {
//        
//    	String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
//	    Collection<? extends GrantedAuthority> loggedInRole = AuthDetailsProvider.getLoggedRole();
//
//    	CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//        CriteriaQuery<SiteMeasurement> query = cb.createQuery(SiteMeasurement.class);
//        Root<SiteMeasurement> root = query.from(SiteMeasurement.class);
//
//        List<Predicate> predicates = new ArrayList<>();
//
//        if (serviceRequestId != null && !serviceRequestId.trim().isEmpty()) {
//            predicates.add(cb.equal(root.get("serviceRequestId"), serviceRequestId));
//        }
//        if (eastSiteLegth != null && !eastSiteLegth.trim().isEmpty()) {
//            predicates.add(cb.equal(root.get("eastSiteLegth"), eastSiteLegth));
//        }
//        if (location != null && !location.trim().isEmpty()) {
//            predicates.add(cb.equal(root.get("location"), location));
//        }
//        if (userId != null && !userId.trim().isEmpty()) {
//            predicates.add(cb.equal(root.get("userId"), userId));
//        }
//        if (updatedBy != null && !updatedBy.trim().isEmpty()) {
//            predicates.add(cb.equal(root.get("updatedBy"), updatedBy));
//        }
//
//        query.select(root);
//        if (!predicates.isEmpty()) {
//            query.where(cb.and(predicates.toArray(new Predicate[0])));
//        }
//
//        TypedQuery<SiteMeasurement> typedQuery = entityManager.createQuery(query);
//        typedQuery.setFirstResult((int) pageable.getOffset());
//        typedQuery.setMaxResults(pageable.getPageSize());
//
//        // For total count
//        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
//        Root<SiteMeasurement> countRoot = countQuery.from(SiteMeasurement.class);
//
//        List<Predicate> countPredicates = new ArrayList<>();
//        if (serviceRequestId != null && !serviceRequestId.trim().isEmpty()) {
//            countPredicates.add(cb.equal(countRoot.get("serviceRequestId"), serviceRequestId));
//        }
//        if (eastSiteLegth != null && !eastSiteLegth.trim().isEmpty()) {
//            countPredicates.add(cb.equal(countRoot.get("eastSiteLegth"), eastSiteLegth));
//        }
//        if (location != null && !location.trim().isEmpty()) {
//            countPredicates.add(cb.equal(countRoot.get("location"), location));
//        }
//        if (userId != null && !userId.trim().isEmpty()) {
//        	countPredicates.add(cb.equal(countRoot.get("userId"), userId));
//        }
//        if (updatedBy != null && !updatedBy.trim().isEmpty()) {
//            countPredicates.add(cb.equal(countRoot.get("updatedBy"), updatedBy));
//        }
//
//        countQuery.select(cb.count(countRoot));
//        if (!countPredicates.isEmpty()) {
//            countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));
//        }
//
//        Long total = entityManager.createQuery(countQuery).getSingleResult();
//
//        return new PageImpl<>(typedQuery.getResultList(), pageable, total);
//    }

	@Override
	public Page<SiteMeasurement> getSiteMeasurement(String serviceRequestId, String eastSiteLegth, String location,
			String userId, Date fromRequestDate, Date toRequestDate, Pageable pageable) {
		// Step 4: Build Criteria Query
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<SiteMeasurement> query = cb.createQuery(SiteMeasurement.class);
		Root<SiteMeasurement> root = query.from(SiteMeasurement.class);

		List<Predicate> predicates = new ArrayList<>();

		if (serviceRequestId != null && !serviceRequestId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("serviceRequestId"), serviceRequestId));
		}
		if (eastSiteLegth != null && !eastSiteLegth.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("eastSiteLegth"), eastSiteLegth));
		}
		if (location != null && !location.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("location"), location));
		}

		// Step 5: Use request-provided userId if present, else fallback to
		// loggedInUserId
		if (userId != null && !userId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("userId"), userId));
		}
		if (fromRequestDate != null && toRequestDate != null) {
			predicates.add(cb.between(root.get("requestDate"), fromRequestDate, toRequestDate));
		} else if (fromRequestDate != null) {
			predicates.add(cb.greaterThanOrEqualTo(root.get("requestDate"), fromRequestDate));
		} else if (toRequestDate != null) {
			predicates.add(cb.lessThanOrEqualTo(root.get("requestDate"), toRequestDate));
		}

		query.select(root);
		if (!predicates.isEmpty()) {
			query.where(cb.and(predicates.toArray(new Predicate[0])));
		}

		// Step 7: Pagination
		TypedQuery<SiteMeasurement> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult((int) pageable.getOffset());
		typedQuery.setMaxResults(pageable.getPageSize());

		// Step 8: Count query for total
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<SiteMeasurement> countRoot = countQuery.from(SiteMeasurement.class);

		List<Predicate> countPredicates = new ArrayList<>();
		if (serviceRequestId != null && !serviceRequestId.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("serviceRequestId"), serviceRequestId));
		}
		if (eastSiteLegth != null && !eastSiteLegth.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("eastSiteLegth"), eastSiteLegth));
		}
		if (location != null && !location.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("location"), location));
		}
		if (userId != null && !userId.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("userId"), userId));
		}

		if (fromRequestDate != null && toRequestDate != null) {
			countPredicates.add(cb.between(countRoot.get("requestDate"), fromRequestDate, toRequestDate));
		} else if (fromRequestDate != null) {
			countPredicates.add(cb.greaterThanOrEqualTo(countRoot.get("requestDate"), fromRequestDate));
		} else if (toRequestDate != null) {
			countPredicates.add(cb.lessThanOrEqualTo(countRoot.get("requestDate"), toRequestDate));
		}

		countQuery.select(cb.count(countRoot));
		if (!countPredicates.isEmpty()) {
			countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));
		}

		Long total = entityManager.createQuery(countQuery).getSingleResult();

		// Step 9: Return paginated result
		return new PageImpl<>(typedQuery.getResultList(), pageable, total);
	}

	@Override
	public SiteMeasurement findByServiceRequestId(String serviceRequestId) {
		return repository.findByServiceRequestId(serviceRequestId);
	}

	@Override
	public UpdateSiteMeasurementStatusResponseDTO updateStatus(UpdateSiteMeasurementStatusRequestDTO dto, RegSource regSource) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Collection<? extends GrantedAuthority> loggedInRole = AuthDetailsProvider.getLoggedRole();

		List<String> roleNames = loggedInRole.stream().map(GrantedAuthority::getAuthority)
				.map(role -> role.replace("ROLE_", "")).collect(Collectors.toList());

		if (roleNames.equals("Developer")) {
			throw new ResourceNotFoundException("Restricted role: " + roleNames);
		}

		UserType userType = UserType.valueOf(roleNames.get(0));

		// User identity variables
		String userId;

		if (userType == UserType.EC) {
			// EC User
			CustomerRegistration customer = repo.findByUserEmailAndUserType(loggedInUserEmail, userType)
					.orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + loggedInUserEmail));

			userId = customer.getUserid();

		} else if (userType == UserType.Adm) {
			// Admin User
			AdminDetails admin = adminRepo.findByEmailAndUserType(loggedInUserEmail, userType)
					.orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + loggedInUserEmail));

			userId = admin.getEmail();// or any other logic you want to follow

		} else {
			// Other user types
			User user = userDAO.findByEmailAndUserTypeAndRegSource(loggedInUserEmail, userType, regSource)
					.orElseThrow(() -> new ResourceNotFoundException("User not found: " + loggedInUserEmail));

			userId = user.getBodSeqNo(); // modify if needed
		}

		SiteMeasurement siteOpt = repository.findByServiceRequestId(dto.getServiceRequestId());
		if (siteOpt == null) {
			throw new RuntimeException("Invalid serviceRequestId. No SiteMeasurement found.");
		}

		// Save to update_site_measurement_status table
		UpdateSiteMeasurementStatus update = new UpdateSiteMeasurementStatus();
		update.setServiceRequestId(dto.getServiceRequestId());
		update.setUpdatedBy(userId);
		update.setStatus(dto.getStatus());
		update.setComments(dto.getComments());
		update.setUpdatedDate(new Date());
		updateSMSR.save(update);

		// Build and return response DTO
		UpdateSiteMeasurementStatusResponseDTO response = new UpdateSiteMeasurementStatusResponseDTO();
		response.setServiceRequestId(update.getServiceRequestId());
		response.setUpdatedBy(update.getUpdatedBy());
		response.setStatus(update.getStatus());
		response.setComments(update.getComments());
		response.setUpdatedDate(update.getUpdatedDate());

		return response;
	}
}