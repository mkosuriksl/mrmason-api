package com.application.mrmason.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
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

import com.application.mrmason.dto.MeasurementDTO;
import com.application.mrmason.dto.ServiceRequestItem;
import com.application.mrmason.entity.AdminDetails;
import com.application.mrmason.entity.SPWAStatus;
import com.application.mrmason.entity.ServiceRequestPaintQuotation;
import com.application.mrmason.entity.ServiceRequestPlumbingQuotation;
import com.application.mrmason.entity.ServiceRequestQuotation;
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.AdminDetailsRepo;
import com.application.mrmason.repository.ServiceRequestPaintQuotationRepository;
import com.application.mrmason.repository.ServiceRequestQuotationRepository;
import com.application.mrmason.repository.SiteMeasurementRepository;
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
	private SiteMeasurementRepository serviceRequestRepo;
	
	@Autowired
	private ServiceRequestPaintQuotationRepository serviceRequestPaintQuotationRepository;

	@Autowired
	ServiceRequestQuotationRepository serviceRequestQuotationAuditRepository;

	@Override
	public List<ServiceRequestPaintQuotation> createServiceRequestPaintQuotationService(
	        String requestId, String serviceCategory, List<ServiceRequestItem> items, RegSource regSource) {

	    UserInfo userInfo = getLoggedInUserInfo(regSource);

	    Map<String, Integer> taskCounters = new HashMap<>(); // counter per taskId
	    List<ServiceRequestPaintQuotation> savedQuotations = new ArrayList<>();
	    double totalQuotationAmount = 0.0;

	    for (ServiceRequestItem item : items) {
	        String taskId = item.getTaskId();
	        taskCounters.putIfAbsent(taskId, 0); // initialize if not present

	        for (MeasurementDTO measurement : item.getMeasurements()) {
	            int currentCounter = taskCounters.compute(taskId, (k, v) -> v + 1); // increment for this taskId
	            String lineId = taskId + "_" + String.format("%04d", currentCounter); // e.g., PLUMBING_0001

	            ServiceRequestPaintQuotation sRPQ = new ServiceRequestPaintQuotation();
	            sRPQ.setAdmintasklineId(lineId); // ✅ Fix: Set primary key manually
	            sRPQ.setRequestId(requestId);
	            sRPQ.setTaskId(taskId); // Save original taskId if needed
	            sRPQ.setTaskDescription(item.getTaskDescription() != null 
	                ? item.getTaskDescription() 
	                : item.getTaskId());
	            sRPQ.setServiceCategory(serviceCategory);
	            sRPQ.setQuotedDate(new Date());
	            sRPQ.setStatus(SPWAStatus.NEW);
	            sRPQ.setSpId(userInfo.userId);
	            sRPQ.setUpdatedBy(userInfo.userId);
	            sRPQ.setUpdatedDate(new Date());
	            sRPQ.setMeasureNames(measurement.getMeasureNames());
	            sRPQ.setValue(measurement.getValue());
	            if ("quotationAmount".equalsIgnoreCase(measurement.getMeasureNames())) {
	                try {
	                    totalQuotationAmount += Double.parseDouble(measurement.getValue());
	                } catch (NumberFormatException e) {
	                    // log error
	                }
	            }

	            savedQuotations.add(serviceRequestPaintQuotationRepository.save(sRPQ));
	        }
	    }

	    // Audit logic (unchanged)
	    List<ServiceRequestQuotation> existingAuditOpt = serviceRequestQuotationAuditRepository.findByRequestId(requestId);
	    ServiceRequestQuotation audit = existingAuditOpt.isEmpty() ? new ServiceRequestQuotation() : existingAuditOpt.get(0);

	    audit.setRequestId(requestId);
	    audit.setQuotedDate(new Date());
	    audit.setUpdatedBy(userInfo.userId);
	    audit.setUpdatedDate(new Date());
	    audit.setQuotatedBy(userInfo.userId);
	    audit.setStatus(SPWAStatus.NEW);

	    if (totalQuotationAmount > 0) {
	        audit.setMeasureNames("quotationAmount");
	        audit.setValue(String.valueOf(totalQuotationAmount));
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
	public Page<ServiceRequestPaintQuotation> getServiceRequestPaintQuotationService(
			String admintasklineId, String taskDescription, String taskId,String serviceCategory,
			String measureNames,String status,String spId,Pageable pageable) {

	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

	    // === Main query ===
	    CriteriaQuery<ServiceRequestPaintQuotation> query = cb.createQuery(ServiceRequestPaintQuotation.class);
	    Root<ServiceRequestPaintQuotation> root = query.from(ServiceRequestPaintQuotation.class);
	    List<Predicate> predicates = new ArrayList<>();

	    if (admintasklineId != null && !admintasklineId.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("admintasklineId"), admintasklineId));
	    }
	    if (taskDescription != null && !taskDescription.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("taskDescription"), taskDescription));
	    }
	    if (taskId != null && !taskId.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("taskId"), taskId));
	    }
	    if (measureNames != null ) {
	        predicates.add(cb.equal(root.get("measureNames"), measureNames));
	    }
	    if (status != null && !status.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("status"), status));
	    }
	    if (spId != null && !spId.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("spId"), spId));
	    }
	    if (serviceCategory != null && !serviceCategory.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("serviceCategory"), serviceCategory));
	    }
	    query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
	    TypedQuery<ServiceRequestPaintQuotation> typedQuery = entityManager.createQuery(query);
	    typedQuery.setFirstResult((int) pageable.getOffset());
	    typedQuery.setMaxResults(pageable.getPageSize());

	    // === Count query ===
	    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
	    Root<ServiceRequestPaintQuotation> countRoot = countQuery.from(ServiceRequestPaintQuotation.class);
	    List<Predicate> countPredicates = new ArrayList<>();

	    if (admintasklineId != null && !admintasklineId.trim().isEmpty()) {
	    	countPredicates.add(cb.equal(countRoot.get("admintasklineId"), admintasklineId));
	    }
	    if (taskDescription != null && !taskDescription.trim().isEmpty()) {
	    	countPredicates.add(cb.equal(countRoot.get("taskDescription"), taskDescription));
	    }
	    if (taskId != null && !taskId.trim().isEmpty()) {
	    	countPredicates.add(cb.equal(countRoot.get("taskId"), taskId));
	    }
	    if (measureNames != null ) {
	    	countPredicates.add(cb.equal(countRoot.get("measureNames"), measureNames));
	    }
	    if (status != null && !status.trim().isEmpty()) {
	    	countPredicates.add(cb.equal(countRoot.get("status"), status));
	    }
	    if (spId != null && !spId.trim().isEmpty()) {
	    	countPredicates.add(cb.equal(countRoot.get("spId"), spId));
	    }
	    if (serviceCategory != null && !serviceCategory.trim().isEmpty()) {
	    	countPredicates.add(cb.equal(countRoot.get("serviceCategory"), serviceCategory));
	    }
	    countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
	    Long total = entityManager.createQuery(countQuery).getSingleResult();

	    return new PageImpl<>(typedQuery.getResultList(), pageable, total);
	}

	@Override
	public List<ServiceRequestPaintQuotation> updateServiceRequestQuotation(String taskId,
	        List<ServiceRequestPaintQuotation> dtoList, RegSource regSource) {

	    UserInfo userInfo = getLoggedInUserInfo(regSource);

	    // Step 1: Load existing quotations by taskId
	    Map<String, ServiceRequestPaintQuotation> existingMap = serviceRequestPaintQuotationRepository
	            .findByTaskId(taskId)
	            .stream()
	            .collect(Collectors.toMap(ServiceRequestPaintQuotation::getAdmintasklineId, Function.identity()));

	    List<ServiceRequestPaintQuotation> updatedQuotations = new ArrayList<>();
	    double totalQuotationAmount = 0.0;
	    String requestId = null;
	    SPWAStatus status = !dtoList.isEmpty() ? dtoList.get(0).getStatus() : SPWAStatus.NEW;

	    for (ServiceRequestPaintQuotation dto : dtoList) {
	        String admintasklineId = dto.getAdmintasklineId();
	        String lineTaskId = admintasklineId.split("_")[0];

	        // Validate admintasklineId prefix
	        if (!lineTaskId.equals(taskId)) {
	            throw new IllegalArgumentException("Invalid admintasklineId: " + admintasklineId +
	                    " does not match taskId: " + taskId);
	        }

	        ServiceRequestPaintQuotation existing = existingMap.get(admintasklineId);
	        if (existing != null) {
	            // Update relevant field
	            existing.setQuotedDate(new Date());
	            existing.setStatus(dto.getStatus());
	            existing.setUpdatedBy(userInfo.userId);
	            existing.setUpdatedDate(new Date());

	            if ("quotationAmount".equalsIgnoreCase(dto.getMeasureNames())) {
	                try {
	                    totalQuotationAmount += Double.parseDouble(dto.getValue());
	                } catch (NumberFormatException e) {
	                    // You can log a warning here
	                }
	            }

	            requestId = existing.getRequestId(); // Save for audit header update

	            updatedQuotations.add(serviceRequestPaintQuotationRepository.save(existing));
	        }
	    }

	    // Step 2: Update or create ServiceRequestQuotation audit/header
	    if (requestId != null) {
	        List<ServiceRequestQuotation> optionalHeader = serviceRequestQuotationAuditRepository.findByRequestIds(requestId);
	        ServiceRequestQuotation header;

	        if (!optionalHeader.isEmpty()) {
	            header = optionalHeader.get(0);
	        } else {
	            header = new ServiceRequestQuotation();
	            header.setRequestId(requestId);
	            header.setQuotedDate(new Date());
	            header.setQuotatedBy(userInfo.userId);
	        }

	        if (totalQuotationAmount > 0) {
	            header.setMeasureNames("quotationAmount");
	            header.setValue(String.valueOf(totalQuotationAmount));
	        }

	        header.setStatus(status);
	        header.setUpdatedBy(userInfo.userId);
	        header.setUpdatedDate(new Date());

	        serviceRequestQuotationAuditRepository.save(header);
	    }

	    return updatedQuotations;
	}

}
